package io.zbox.fs;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class VersionReader extends RustObject {

    private static int rustObjId = 104;

    private VersionReader() { }

    public long read(@NonNull ByteBuffer dst) throws ZboxException {
        checkNullParam(dst);

        if (dst.isDirect()) {
            long ret = this.jniRead(dst.slice());
            dst.position(dst.position() + (int)ret);
            return ret;
        }

        ByteBuffer cloned = ByteBuffer.allocateDirect(dst.remaining());
        long ret = this.jniRead(cloned);
        cloned.position((int)ret);
        cloned.flip();
        dst.put(cloned);
        return ret;
    }

    public long seek(long offset, @NonNull SeekFrom whence) throws ZboxException {
        checkNullParam(whence);
        return this.jniSeek(offset, whence.getValue());
    }

    // jni methods
    private native long jniRead(ByteBuffer dst);
    private native long jniSeek(long offset, int whence);
}


