package io.zbox.fs;

import java.nio.ByteBuffer;

public class File extends RustObject {

    private static int rustObjId = 103;

    private File() {}

    public Metadata metadata() {
        return this.jniMetadata();
    }

    public Version[] history() {
        return this.jniHistory();
    }

    public long currVersion() {
        return this.jniCurrVersion();
    }

    public VersionReader versionReader(long verNum) {
        return this.jniVersionReader(verNum);
    }

    public void finish() throws ZboxException {
        this.jniFinish();
    }

    public void writeOnce(ByteBuffer buf) throws ZboxException {
        this.jniWriteOnce(buf);
    }

    public void setLen(long len) throws ZboxException {
        this.jniSetLen(len);
    }

    public long read(ByteBuffer dst) throws ZboxException {
        return this.jniRead(dst);
    }

    public long write(ByteBuffer buf) throws ZboxException {
        return this.jniWrite(buf);
    }

    public long seek(long offset, SeekFrom whence) throws ZboxException {
        return this.jniSeek(offset, whence.getValue());
    }

    // jni methods
    private native Metadata jniMetadata();
    private native Version[] jniHistory();
    private native long jniCurrVersion();
    private native VersionReader jniVersionReader(long verNum);
    private native void jniFinish();
    private native void jniWriteOnce(ByteBuffer buf);
    private native void jniSetLen(long len);
    private native long jniRead(ByteBuffer dst);
    private native long jniWrite(ByteBuffer buf);
    private native long jniSeek(long offset, int whence);
}
