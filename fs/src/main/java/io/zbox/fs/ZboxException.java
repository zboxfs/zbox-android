package io.zbox.fs;

/**
 * Exception to indicate that there is an error happened during operations in ZboxFS.
 *
 * @author Bo Lu
 */
public class ZboxException extends Exception {

    public ZboxException() {
        super();
    }

    public ZboxException(String message) {
        super(message);
    }

    public ZboxException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZboxException(Throwable cause) {
        super(cause);
    }
}
