package io.zbox.fs;

public final class Env {
    public static native int init();

    static {
        System.loadLibrary("zbox");
    }
}
