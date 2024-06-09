package patrick.response;

import java.time.LocalDateTime;

class Response
{
    var status: Int;
    var date: LocalDateTime;
    var server: String;
    var connection: String;
    var content_type: String;
    var content_length: Int;

    constructor(status: Int, date: LocalDateTime, server: String, conn: String, cont_typ: String, cont_len: Int)
    {
        this.status = status;
        this.date = date; 
        this.server = server;
        this.connection = conn;
        this.content_type = cont_typ;
        this.content_length = cont_len;
    }

    fun send(data: String)
    {
        println(data);
    }
};