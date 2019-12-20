package io.zbox.zboxfs;

import android.util.Log;

/**
 * Exception to indicate that there is an error happened during operations in
 * ZboxFS.
 *
 * @author Bo Lu
 */
public class ZboxException extends Exception {

    private static final long serialVersionUID = 30974L;

    // error code constants
    //
    // for the full list of error codes, visit https://github.com/zboxfs/zbox/blob/master/src/error.rs
    public static final int ERR_REF_OVERFLOW = -1000;
    public static final int ERR_REF_UNDERFLOW = -1001;
    public static final int ERR_INIT_CRYPTO = -1010;
    public static final int ERR_NO_AES_HARDWARE = -1011;
    public static final int ERR_HASHING = -1012;
    public static final int ERR_INVALID_COST = -1013;
    public static final int ERR_INVALID_CIPHER = -1014;
    public static final int ERR_ENCRYPT = -1015;
    public static final int ERR_DECRYPT = -1016;
    public static final int ERR_INVALID_URI = -1020;
    public static final int ERR_INVALID_SUPERBLK = -1021;
    public static final int ERR_CORRUPTED = -1022;
    public static final int ERR_WRONG_VERSION = -1023;
    public static final int ERR_NO_ENTITY = -1024;
    public static final int ERR_NOT_IN_SYNC = -1025;
    public static final int ERR_REPO_OPENED = -1026;
    public static final int ERR_REPO_CLOSED = -1027;
    public static final int ERR_REPO_EXISTS = -1028;
    public static final int ERR_IN_TRANS = -1030;
    public static final int ERR_NOT_IN_TRANS = -1031;
    public static final int ERR_NO_TRANS = -1032;
    public static final int ERR_UNCOMPLETED = -1033;
    public static final int ERR_IN_USE = -1034;
    public static final int ERR_NO_CONTENT = -1040;
    public static final int ERR_INVALID_ARGUMENT = -1050;
    public static final int ERR_INVALID_PATH = -1051;
    public static final int ERR_NOT_FOUND = -1052;
    public static final int ERR_ALREADY_EXISTS = -1053;
    public static final int ERR_IS_ROOT = -1054;
    public static final int ERR_IS_DIR = -1055;
    public static final int ERR_IS_FILE = -1056;
    public static final int ERR_NO_TDIR = -1057;
    public static final int ERR_NO_TFILE = -1058;
    public static final int ERR_NOT_EMPTY = -1059;
    public static final int ERR_NO_VERSION = -1060;
    public static final int ERR_READ_ONLY = -1070;
    public static final int ERR_CANNOT_READ = -1071;
    public static final int ERR_CANNOT_WRITE = -1072;
    public static final int ERR_NOT_WRITE = -1073;
    public static final int ERR_NOT_FINISH = -1074;
    public static final int ERR_CLOSED = -1075;
    public static final int ERR_ENCODE = -2000;
    public static final int ERR_DECODE = -2010;
    public static final int ERR_VAR = -2020;
    public static final int ERR_IO = -2030;
    public static final int ERR_SQLITE = -2040;
    public static final int ERR_REDIS = -2050;
    public static final int ERR_HTTP = -2060;
    public static final int ERR_HTTP_STATUS = -2061;
    public static final int ERR_JSON = -2062;
    public static final int ERR_REQWEST = -2063;
    public static final int ERR_JNI = -2064;
    public static final int ERR_REQUEST_ERROR = -2065;

    // error code
    private int errorCode = 0;

    public ZboxException() { super(); }

    public ZboxException(String message) { super(message); }

    public ZboxException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ZboxException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZboxException(Throwable cause) {
        super(cause);
    }

    /**
     * Get the unique error code of this exception.
     *
     * @return the error code
     */
    public int getErrorCode() { return errorCode; }
}
