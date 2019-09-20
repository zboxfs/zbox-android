package io.zbox.fs;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class File extends RustObject {

    private static int rustObjId = 103;

    private int READ_BUF_CAP = 64 * 1024;
    private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUF_CAP);

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

    public void writeOnce(@NonNull ByteBuffer buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer src = this.ensureDirectBuf(buf);
        this.jniWriteOnce(src);
    }

    public void setLen(long len) throws ZboxException {
        this.jniSetLen(len);
    }

    public long read(@NonNull ByteBuffer dst) throws ZboxException {
        checkNullParam(dst);

        if (dst.isDirect()) {
            long ret = this.jniRead(dst.slice());
            dst.position(dst.position() + (int)ret);
            return ret;
        }

        ByteBuffer cloned = ByteBuffer.allocateDirect(dst.remaining());
        long ret = this.jniRead(cloned);
        cloned.limit((int)ret);
        dst.put(cloned);
        return ret;
    }

    public int read(byte[] dst, int off, int len) throws ZboxException {
        ByteBuffer buf;

        if (len > READ_BUF_CAP) {
            buf = ByteBuffer.allocateDirect(len);
        } else {
            buf = this.readBuf.duplicate();
            buf.clear();
            buf.limit(len);
            buf = buf.slice();
        }

        int ret = (int)this.jniRead(buf);
        buf.limit(ret);
        buf.get(dst, off, len > ret ? ret : len);

        return ret;
    }

    public int read(byte[] dst) throws ZboxException {
        return this.read(dst, 0, dst.length);
    }

    public ByteBuffer readAll() throws ZboxException {
        ByteBuffer ret = this.jniReadAll();
        ret.position(ret.limit());
        return ret;
    }

    public long write(@NonNull ByteBuffer buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer src = this.ensureDirectBuf(buf);
        return this.jniWrite(src);
    }

    public long seek(long offset, @NonNull SeekFrom whence) throws ZboxException {
        checkNullParam(whence);
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
    private native ByteBuffer jniReadAll();
    private native long jniWrite(ByteBuffer buf);
    private native long jniSeek(long offset, int whence);
}
