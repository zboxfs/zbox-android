package io.zbox.zboxfs;

import java.nio.ByteBuffer;

abstract class RustObject implements AutoCloseable {
    // pointer to Rust object
    private long rustObj = 0;

    RustObject() {
        this.jniSetRustObj();
    }

    /**
     * Closes this instance and releases any resources associated with this
     * instance.
     */
    public void close() {
        if (this.rustObj != 0) {
            this.jniTakeRustObj();
            if (rustObj != 0) {
                throw new AssertionError("Rust object pointer must be null after release");
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    static void checkNullParam(Object param) throws ZboxException {
        if (param == null)
            throw new ZboxException("Invalid null parameter");
    }

    static void checkNullParam2(Object param, Object param2) throws ZboxException {
        if (param == null || param2 == null)
            throw new ZboxException("Invalid null parameter");
    }

    ByteBuffer ensureDirectBuf(ByteBuffer buf) {
        ByteBuffer src = buf.asReadOnlyBuffer();
        src.flip();

        if (buf.isDirect()) {
            return src.slice();
        }

        ByteBuffer cloned = ByteBuffer.allocateDirect(src.limit());
        cloned.put(src);
        return cloned;
    }

    // jni methods
    private native void jniSetRustObj();

    private native void jniTakeRustObj();
}
