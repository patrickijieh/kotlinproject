import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import patrick.server.*;

const val PORT = 3000;

fun main(args: Array<String>)
{
    val server: Server = Server(3000);
    if (args.size > 0)
        return;

    server.get("/", {req: Request, res: Response ->
        res.send("Hello World!");
    });

    server.start({
        println("Server is running on port " + server.port());
    });
}
