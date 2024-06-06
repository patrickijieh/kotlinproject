package patrick;

import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.io.InputStream;

const val HTTP_VERSION_LENGTH: Int = 11;

class Server
{
    private val _server: ServerSocket = ServerSocket();

    fun getInstance(): ServerSocket { return _server; }

    fun port(): Int { return _server.getLocalPort(); }

    constructor( port: Int = 80 )
    {
        if ( _server.getLocalPort() == -1 )
        {
            _server.bind( InetSocketAddress(port) );
        }
    }

    fun start( callback: () -> Unit = {} )
    {
        callback();
        this.run();
    }

    private fun run()
    {
        while (true)
        {
            val client = _server.accept();
            if ( client != null )
            {
                println( "Client connected: " + client.inetAddress.hostAddress );
                var stream = client.getInputStream();
                // if (stream.available() <= 0)
                //     break;
            
                this.readInputStream(stream);
            }
        }
    }

    fun close()
    {
        _server.close();
    }

    private fun readInputStream( stream: InputStream )
    {
        while ( stream.available() > 0 )
        {
            var size = stream.available();
            var buf = Array<Byte>(size) { 0 }.toByteArray();
            stream.read(buf);
            for ( b in buf )
            {
                if ( b < 0 )
                    return;
                
                print( b.toInt().toChar() );
            }

            this.parseRequest(buf);
        }
    }

    private fun parseRequest( data: ByteArray ): Request
    {
        var method: HTTPMethod = HTTPMethod.None;
        var route: String = "";
        var host: String = "";
        var user_agent: String = "";
        var accept: Array<String> = arrayOf("*/*");
        var accept_language: String = "";
        var accept_encoding: String = "";
        var connection: String = "";
        var content_type: String = "";
        var content_length: Int = 0;

        var req = Request(
            method, route, host, user_agent,
            accept, accept_language, accept_encoding,
            connection, content_type, content_length
            );
        
        // find method
        if ( data[0] != 71.toByte() /* G(ET) */
            && data[0] != 80.toByte() /* P(OST) or P(ATCH) */
            && data[0] != 68.toByte() /* D(ELETE) */
            )
            {
                return req;
            }

        
        method = this.parseMethod(data);

        var pos: Int = method.toString().length + 1;
        route = this.parseRoute(data, pos);

        pos = pos + route.length + HTTP_VERSION_LENGTH;
        
        
        println(method);
        println(route);
        println(data[pos].toInt().toChar());

        return req;
    }

    private fun parseMethod( data: ByteArray ): HTTPMethod
    {
        if ( data.size < 7 )
        {
            return HTTPMethod.None;
        }

        var str: StringBuilder = StringBuilder();
        
        for ( i in 0..5 )
        {
            str.append(data[i].toInt().toChar());
        }

        if ( str.substring(0, 4) == "GET " )
            return HTTPMethod.GET;

        else if ( str.substring(0, 5) == "POST " )
            return HTTPMethod.POST;
        
        else if ( str.substring(0, 6) == "PATCH " )
            return HTTPMethod.PATCH;
        
        else if ( str.substring(0, 7) == "DELETE " )
            return HTTPMethod.DELETE;

        else
            return HTTPMethod.None;
    }

    private fun parseRoute(data: ByteArray, pos: Int): String
    {
        var route: StringBuilder = StringBuilder();
        var curr_pos = pos;
        while ( data.get(curr_pos) != 32.toByte() )
        {
            route.append( data.get(pos).toInt().toChar() );
            curr_pos++;
        }
        return route.toString();
    }
}



class Request
(
    var method: HTTPMethod,
    var route: String,
    var host: String,
    var user_agent: String,
    var accept: Array<String>,
    var accept_language: String,
    var accept_encoding: String,
    var connection: String,
    var content_type: String,
    var content_length: Int,
)

enum class HTTPMethod
{
    None,
    GET,
    POST,
    PATCH,
    DELETE
}