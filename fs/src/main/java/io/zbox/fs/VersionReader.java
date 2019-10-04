package io.zbox.fs;

import java.nio.ByteBuffer;

public class VersionReader extends RustObject {

    private static int rustObjId = 104;

    // read buffer
    private int READ_BUF_CAP = 16 * 1024;
    private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUF_CAP);

    private VersionReader() {
    }

    public long read(ByteBuffer dst) throws ZboxException {
        checkNullParam(dst);

        if (dst.isDirect()) {
            long ret = this.jniRead(dst.slice());
            dst.position(dst.position() + (int) ret);
            return ret;
        }

        ByteBuffer cloned = ByteBuffer.allocateDirect(dst.remaining());
        long ret = this.jniRead(cloned);
        cloned.limit((int) ret);
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

        int ret = (int) this.jniRead(buf);
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

    public long seek(long offset, SeekFrom whence) throws ZboxException {
        checkNullParam(whence);
        return this.jniSeek(offset, whence.getValue());
    }

    // jni methods
    private native long jniRead(ByteBuffer dst) throws ZboxException;

    private native ByteBuffer jniReadAll() throws ZboxException;

    private native long jniSeek(long offset, int whence) throws ZboxException;
}


