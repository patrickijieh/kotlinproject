package patrick.request;

import patrick.http_methods.HTTPMethod;

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
    var content_length: Int
);