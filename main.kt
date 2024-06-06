import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import patrick.*;

const val PORT = 3000;

fun main(args: Array<String>)
{
    val server: Server = Server(3000);
    if (args.size > 0)
        return;
    
    server.start({
        println("Server is running on port " + server.port())
    });

    server.close();
}