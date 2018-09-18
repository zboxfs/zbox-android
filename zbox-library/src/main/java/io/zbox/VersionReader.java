package io.zbox;

import java.nio.ByteBuffer;

public class VersionReader extends RustObject {

    private static int rustObjId = 104;

    private VersionReader() { }

    public long read(ByteBuffer dst) throws ZboxException {
        return this.jniRead(dst);
    }

    public long seek(long offset, SeekFrom whence) throws ZboxException {
        return this.jniSeek(offset, whence.getValue());
    }

    // jni methods
    private native long jniRead(ByteBuffer dst);
    private native long jniSeek(long offset, int whence);
}


