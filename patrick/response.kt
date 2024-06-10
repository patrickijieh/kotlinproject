package patrick.response;

import java.time.ZonedDateTime;

class Response
{
    var ready: Boolean;
    var status_code: Int;
    var date: ZonedDateTime;
    var server: String;
    var modified: ZonedDateTime;
    var connection: String;
    var content_type: String;
    var content_length: Int;
    var body: String;
    

    constructor(status: Int, date: ZonedDateTime, server: String, mod: ZonedDateTime, conn: String, cont_typ: String, cont_len: Int, body: String)
    {
        this.ready = false;
        this.status_code = status;
        this.date = date; 
        this.server = server;
        this.modified = mod;
        this.connection = conn;
        this.content_type = cont_typ;
        this.content_length = cont_len;
        this.body = body;
    }

    fun send(data: String)
    {
        this.body = data;
        this.content_length = body.length;
        this.ready = true;
    }

    fun brick()
    {
        this.ready = true;
    }

    fun status(s: Int)
    {
        this.status_code = s;
    }
};