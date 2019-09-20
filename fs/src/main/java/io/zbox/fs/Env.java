package io.zbox.fs;

import androidx.annotation.NonNull;

public final class Env {
    // log levels
    public static String LOG_ERROR = "Error";
    public static String LOG_WARN = "Warn";
    public static String LOG_INFO = "Info";
    public static String LOG_DEBUG = "Debug";
    public static String LOG_TRACE = "Trace";

    public static int init(@NonNull String logLevel) {
        String lvl = logLevel == null ? "Warn" : logLevel;
        return initEnv(lvl);
    }

    public static native int initEnv(String logLevel);
    public static native String version();

    static {
        System.loadLibrary("zboxfs");
    }
}
