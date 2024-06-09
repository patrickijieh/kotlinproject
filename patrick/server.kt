package patrick.server;

import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.io.InputStream;
import patrick.headers_iterator.HeadersIterator;

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

    private fun parseRequest( data: ByteArray ): Request?
    {
        var method: HTTPMethod = HTTPMethod.None;
        var route: String = "";
        var host: String = "";
        var user_agent: String = "";
        var accept: Array<String> = emptyArray<String>();
        var accept_language: Array<String> = emptyArray<String>();
        var accept_encoding: Array<String> = emptyArray<String>();
        var connection: String = "";
        var content_type: String = "";
        var content_length: Int = 0;

        // find method
        if ( data[0] != 'G'.code.toByte()/* G(ET) */
            && data[0] != 'P'.code.toByte() /* P(OST) or P(ATCH) */
            && data[0] != 'D'.code.toByte() /* D(ELETE) */
            )
            {
                return null;
            }


        method = this.parseMethod(data);

        var pos: Int = method.toString().length + 1;
        route = this.parseRoute(data, pos);

        pos = pos + route.length + HTTP_VERSION_LENGTH;

        var headers: ByteArray = ByteArray(data.size - pos);

        for ( i in 0..headers.size-1 )
        {
            headers[i] = data[i + pos];
        }

        var headersIter: HeadersIterator = HeadersIterator(headers);
        var next = headersIter.next();
        while ( next != null )
        {
            when (next.first)
            {
                0 -> host = next.second;
                1 -> user_agent = next.second;
                2 -> accept = parseValues(next.second);
                3 -> accept_language = parseValues(next.second);
                4 -> accept_encoding = parseValues(next.second);
                5 -> connection = next.second;
                6 -> content_type = next.second;
                7 -> content_length = next.second.toInt();
                else -> {}
            }
            next = headersIter.next();
        }

        var req = Request(
            method, route, host, user_agent,
            accept, accept_language, accept_encoding,
            connection, content_type, content_length
            );
        
        println(content_length);
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

    private fun parseRoute( data: ByteArray, pos: Int ): String
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

    private fun parseValues(values: String): Array<String>
    {
        val ret: ArrayList<String> = ArrayList<String>();
        val current: StringBuilder = StringBuilder();
        for ( i in 0..values.length-1 )
        {
            val char: Char = values.get(i);
            if (char != ',')
            {
                if (char == ' ')
                    continue;

                current.append(values.get(i));
                continue;
            }

            ret.add(current.toString());
            current.clear();
        }

        if (current.length > 0)
            ret.add(current.toString());

        return ret.toTypedArray();
    }
}



class Request
(
    var method: HTTPMethod,
    var route: String,
    var host: String,
    var user_agent: String,
    var accept: Array<String>,
    var accept_language: Array<String>,
    var accept_encoding: Array<String>,
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
