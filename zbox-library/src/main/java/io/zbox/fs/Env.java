package io.zbox.fs;

public final class Env {
    public static native int init();
    public static native String version();

    static {
        System.loadLibrary("zbox");
    }
}
