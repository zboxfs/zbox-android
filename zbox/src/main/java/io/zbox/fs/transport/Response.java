package io.zbox.fs.transport;

// HTTP response wrapper
class Response {

    // response status code
    int status = 0;

    // response body
    byte[] body = null;

    // response body length
    int len = 0;
}
