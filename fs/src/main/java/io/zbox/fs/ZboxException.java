package io.zbox.fs;

import androidx.annotation.NonNull;

public class ZboxException extends Exception {

    public ZboxException() { super(); }

    public ZboxException(@NonNull String message) { super(message); }

    public ZboxException(@NonNull String message, @NonNull Throwable cause) { super(message, cause); }

    public ZboxException(@NonNull Throwable cause) {
        super(cause);
    }
}
