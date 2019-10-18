package io.zbox.zboxfs;

import java.nio.ByteBuffer;

/**
 * A reader for a specific version of file content.
 *
 * <p>This reader can be obtained by {@link File#versionReader(long)} method and must be closed
 * after use.</p>
 *
 * <p>A typical usage pattern is:</p>
 *
 * <ol>
 * <li>Get file history versions using {@link File#history()}</li>
 * <li>Get a version reader for a specific version number using {@link File#versionReader}</li>
 * <li>Read content using {@link #read(ByteBuffer)} or {@link #readAll()}</li>
 * <li>Close the version reader using {@link #close()}</li>
 * </ol>
 *
 * <h3>Example</h3>
 *
 * <blockquote><pre>
 * // get file history versions
 * Version[] hist = file.history();
 *
 * // suppose the history version list is:
 * // [
 * //   { num: 42, contentLen: 123, createdAt: 1540376682 },
 * //   { num: 43, contentLen: 456, createdAt: 1540376683 }
 * // ]
 * // then we can choose version 42 to read from and creating a version reader from it
 * VersionReader vr = file.versionReader(42);
 *
 * // read all content from this version
 * ByteBuffer buf = vr.readAll();
 *
 * // close version reader
 * vr.close();
 * </pre></blockquote>
 *
 * @author Bo Lu
 * @see File
 */
public class VersionReader extends RustObject {

    private static final int rustObjId = 104;

    // read buffer
    private int READ_BUF_CAP = 16 * 1024;
    private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUF_CAP);

    /**
     * Create a version reader instance.
     */
    private VersionReader() {
    }

    /**
     * Pull some bytes from this version reader into the specified buffer, returning how many bytes
     * were read.
     *
     * <p>The bytes are appended to the destination buffer {@code dst}. That is, the writing starts
     * from the current position in {@code dst}.</p>
     *
     * <p>It is recommended to use a direct {@link java.nio.ByteBuffer} to avoid extra memory
     * allocation.</p>
     *
     * @param dst the byte buffer into which bytes are to be written
     * @return number of bytes were read
     * @throws ZboxException if any error happened
     * @see #readAll()
     */
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

    /**
     * Pull some bytes from this version reader into the specified byte array, returning how many
     * bytes were read.
     *
     * <p>This method copies {@code n} bytes from this version reader into the given destination
     * array. If there is no exception thrown, then it must be guaranteed that {@code 0 <= n <= len}.
     * </p>
     *
     * <p>It is recommended to make {@code len} less than 16KB (16384) to avoid extra memory
     * allocation.</p>
     *
     * @param dst the array into which bytes are to be written
     * @param off the offset within the array of the first byte to be written; must be non-negative
     *            and no larger than dst.length
     * @param len the maximum number of bytes to be written to the given array; must be non-negative
     *            and no larger than dst.length - offset
     * @return number of bytes were read
     * @throws ZboxException if any error happened
     */
    public int read(byte[] dst, int off, int len) throws ZboxException {
        checkNullParam(dst);

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

    /**
     * Pull some bytes from this version reader into the specified byte array, returning how many
     * bytes were read.
     *
     * <p>This method copies bytes from this version reader into the given destination array. An
     * invocation of this method of the form {@code file.read(dst)} behaves in exactly the same way
     * as the invocation</p>
     *
     * <blockquote><pre>
     * vr.read(dst, 0, dst.length)
     * </pre></blockquote>
     *
     * <p>It is recommended to make {@code dst.length} less than 16KB (16384) to avoid extra memory
     * allocation.</p>
     *
     * @param dst the array into which bytes are to be written
     * @return number of bytes were read
     * @throws ZboxException if any error happened
     */
    public int read(byte[] dst) throws ZboxException {
        return this.read(dst, 0, dst.length);
    }

    /**
     * Read all bytes until end of the version reader, placing them into the returned buffer.
     *
     * @return the byte buffer holds all read bytes
     * @throws ZboxException if any error happened
     * @see #read(ByteBuffer)
     */
    public ByteBuffer readAll() throws ZboxException {
        ByteBuffer ret = this.jniReadAll();
        ret.position(ret.limit());
        return ret;
    }

    /**
     * Seek to an offset, relative to from in bytes, in this version reader.
     *
     * <p>This method returns the new position from the start of the content. That position can be
     * used later with {@link SeekFrom#START}.</p>
     *
     * <p>A seek beyond the end of the file is allowed. In this case, subsequent write will extend
     * the file and have all of the intermediate data filled in with 0s.</p>
     *
     * @param off    the offset within this file, relative to {@code whence}
     * @param whence the start point to calculate seek offset
     * @return new position from the start of the content
     * @throws ZboxException if any error happened
     * @see SeekFrom
     */
    public long seek(long off, SeekFrom whence) throws ZboxException {
        checkNullParam(whence);
        return this.jniSeek(off, whence.getValue());
    }

    // jni methods
    private native long jniRead(ByteBuffer dst) throws ZboxException;

    private native ByteBuffer jniReadAll() throws ZboxException;

    private native long jniSeek(long offset, int whence) throws ZboxException;
}


