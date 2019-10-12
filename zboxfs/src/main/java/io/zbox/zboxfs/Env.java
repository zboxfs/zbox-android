package io.zbox.zboxfs;

/**
 * This class is to initialise ZboxFS environment.
 *
 * @author Bo Lu
 */
public final class Env {

    /**
     * Log level {@code ERROR}
     */
    public static String LOG_ERROR = "Error";

    /**
     * Log level {@code WARN}
     */
    public static String LOG_WARN = "Warn";

    /**
     * Log level {@code INFO}
     */
    public static String LOG_INFO = "Info";

    /**
     * Log level {@code Debug}
     */
    public static String LOG_DEBUG = "Debug";

    /**
     * Log level {@code TRACE}
     */
    public static String LOG_TRACE = "Trace";

    /**
     * Initialise ZboxFS environment with log level. This method should be called before any other
     * methods provided by ZboxFS and should be called only once.
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
     *
     *        {@code LOG_WARN} is default.
     */
    public static void init(String logLevel) {
        String lvl = logLevel == null ? LOG_WARN : logLevel;
        if (!(lvl.equals(LOG_ERROR) || lvl.equals(LOG_WARN) || lvl.equals(LOG_INFO)
                || lvl.equals(LOG_DEBUG) || lvl.equals(LOG_TRACE)))
        {
            throw new IllegalArgumentException();
        }
        initEnv(lvl);
    }

    /**
     * Get ZboxFS library version string.
     *
     * <p>This method return ZboxFS version as a string, e.g. "ZboxFS v0.8.0".</p>
     *
     * @return ZboxFS library version string
     */
    public static native String version();

    static native void initEnv(String logLevel);

    static {
        System.loadLibrary("zboxfs");
    }
}
