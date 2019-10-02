package io.zbox.fs;

import androidx.annotation.NonNull;

public final class Env {
    // log levels
    public static String LOG_ERROR = "Error";
    public static String LOG_WARN = "Warn";
    public static String LOG_INFO = "Info";
    public static String LOG_DEBUG = "Debug";
    public static String LOG_TRACE = "Trace";

    /**
     * Initialise ZboxFS environment. This method should be called before any other methods provided
     * by ZboxFS and should be called only once.
     *
     * @param logLevel
     *        Log output level. Available options are:
     *
     *        <ul>
     *            <li>Env.LOG_ERROR</li>
     *            <li>Env.LOG_WARN</li>
     *            <li>Env.LOG_INFO</li>
     *            <li>Env.LOG_DEBUG</li>
     *            <li>Env.LOG_TRACE</li>
     *        </ul>
     */
    public static void init(@NonNull String logLevel) {
        String lvl = logLevel == null ? "Warn" : logLevel;
        initEnv(lvl);
    }
    public static native String version();

    static native void initEnv(String logLevel);

    static {
        System.loadLibrary("zboxfs");
    }
}
