package io.zbox.fs.transport;

// HTTP response wrapper
public class Response {

    // response status code
    public int status = 0;

    // response body
    public byte[] body = null;

    // response body length
    public int len = 0;
}
