package io.zbox.fs.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpTransport {

    private static final String LOG_TAG = HttpTransport.class.getName();

    // default timeout, in ms
    private static int timeout = 5000;

    // bytes transfer buffer for JNI
    private static byte[] transBuf = new byte[128 * 1024];

    private HttpTransport() {}

    public static void init(int timeout) {
        HttpTransport.timeout = timeout * 1000;
    }

    private static void setHeaders(HttpsURLConnection conn, HashMap<String, String> headers) {
        for(Map.Entry<String, String> ent : headers.entrySet()) {
            String key = ent.getKey();
            String value = ent.getValue();
            conn.setRequestProperty(key, value);
        }
    }

    public static Response get(URL url, HashMap<String, String> headers) throws IOException {
        HttpsURLConnection conn = null;
        Response ret = new Response();

        try {
            // create connection
            conn = (HttpsURLConnection) url.openConnection();

            // set connection properties
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(timeout);
            conn.setUseCaches(false);
            conn.setDoOutput(false);
            conn.setDoInput(true);

            // set HTTP headers
            setHeaders(conn, headers);

            // send request and get response status code
            ret.status = conn.getResponseCode();

            // only process body when request succeed
            if (ret.status == HttpURLConnection.HTTP_OK) {
                byte[] buf = transBuf;

                // in a very rare situation, the response body is larger than the transfer buffer,
                // then we have to create a larger buffer
                int contentLength = Integer.parseInt(conn.getHeaderField("Content-Length"));
                if (contentLength > transBuf.length) {
                    buf = new byte[contentLength];
                }

                // get response body and read it all to transfer buffer
                InputStream in = conn.getInputStream();
                int read = in.read(buf);
                int totalRead = 0;
                while (read > 0) {
                    totalRead += read;
                    read = in.read(buf, totalRead, buf.length - totalRead);
                }

                ret.body = buf;
                ret.len = totalRead;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return ret;
    }

    public static Response put(URL url, HashMap<String, String> headers, byte[] body) throws IOException {
        HttpsURLConnection conn = null;
        Response ret = new Response();

        try {
            // create connection
            conn = (HttpsURLConnection) url.openConnection();

            // set connection properties
            conn.setRequestMethod("PUT");
            conn.setConnectTimeout(timeout);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(false);

            // set HTTP headers
            setHeaders(conn, headers);

            // write body
            OutputStream out = conn.getOutputStream();
            out.write(body);

            // send request and get response status code
            ret.status = conn.getResponseCode();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return ret;
    }

    public static Response delete(URL url, HashMap<String, String> headers) throws IOException {
        HttpsURLConnection conn = null;
        Response ret = new Response();

        try {
            // create connection
            conn = (HttpsURLConnection) url.openConnection();

            // set connection properties
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(timeout);
            conn.setUseCaches(false);
            conn.setDoOutput(false);
            conn.setDoInput(false);

            // set HTTP headers
            setHeaders(conn, headers);

            // send request and get response status code
            ret.status = conn.getResponseCode();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return ret;
    }
}
