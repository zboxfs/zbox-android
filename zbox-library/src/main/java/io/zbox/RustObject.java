package io.zbox;

abstract class RustObject implements AutoCloseable {
    // pointer to Rust object
    protected long rustObj = 0;

    public RustObject() {
        this.jniSetRustObj();
    }

    public void close() {
        if (this.rustObj != 0) {
            this.jniTakeRustObj();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    // jni methods
    private native void jniSetRustObj();
    private native void jniTakeRustObj();
}
