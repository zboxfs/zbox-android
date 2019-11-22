package io.zbox.zboxfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A reference to an opened file in the repository.
 *
 * <p>An instance of a {@code File} can be read and/or written depending on what options it was
 * opened with. {@code File} also implements {@link SeekFrom} to alter the logical cursor
 * that the file contains internally.</p>
 *
 * <h3>Examples</h3>
 *
 * <p>Create a new file and write bytes to it:</p>
 *
 * <blockquote><pre>
 * File file = repo.createFile("/foo.txt");
 * file.write("Hello, world!".getBytes());
 * file.finish();
 * file.close();
 * </pre></blockquote>
 *
 * <p>Read the content of a file into a {@link java.nio.ByteBuffer}:</p>
 *
 * <blockquote><pre>
 * File file = repo.openFile("/foo.txt");
 * ByteBuffer dst = file.readAll();
 * file.close();
 * </pre></blockquote>
 *
 * <h3>Versioning</h3>
 *
 * <p>{@code File} contents support up to 255 revision versions. {@link Version} is immutable once
 * it is created.</p>
 *
 * <p>By default, the maximum number of versions of a {@code File} is <b>1</b>, which is
 * configurable by {@code version_limit} on both {@link Repo} and {@code File} level. {@code File}
 * level option takes precedence.</p>
 *
 * <p>After reaching this limit, the oldest {@link Version} will be automatically deleted after
 * adding a new one.</p>
 *
 * <p>{@link Version} number starts from <b>1</b> and continuously increases by <b>1</b>.</p>
 *
 * <h3>Writing</h3>
 *
 * <p>{@code File} is multi-versioned, each time updating its content will create a new permanent
 * {@link Version}. There are two ways of writing data to a file:</p>
 *
 * <ul>
 * <li>
 * <h4>Multi-part Write</h4>
 * <p>This is done by updating {@code File} using {@link #write(byte[])} method multiple
 * times. After all writing operations, {@link #finish()} must be called to create a new
 * {@link Version}.</p>
 * <h5>Examples</h5>
 * <blockquote><pre>
 *  File file = new OpenOptions().create(true).open(repo, "/foo.txt");
 *  long written = file.write("My text".getBytes());
 *  written = file.write("My text2".getBytes());
 *  file.finish();
 *         </pre></blockquote>
 * </li>
 * <li>
 * <h4>Single-part Write</h4>
 * <p>This can be done by calling {@link #writeOnce(ByteBuffer)}, which will call
 * {@link #finish()} internally to create a new {@link Version}.</p>
 * <h5>Examples</h5>
 * <blockquote><pre>
 *  File file = new OpenOptions().create(true).open(repo, "/foo.txt");
 *  file.writeOnce("My text".getBytes());
 *         </pre></blockquote>
 * </li>
 * </ul>
 *
 * <h3>Reading</h3>
 *
 * <p>As {@code File} can contain multiple versions, read operation can be associated with different
 * versions. By default, reading on {@code File} object is always bound to the latest version. To
 * read a specific version, a {@link VersionReader}, which supports
 * {@link VersionReader#read(byte[])} as well, can be used.</p>
 *
 * <h4>Examples</h4>
 *
 * <p>Read the file content while it is in writing, notice that reading is always bound to latest
 * content version.</p>
 *
 * <blockquote><pre>
 * byte[] buf = {1, 2, 3, 4, 5, 6};
 * byte[] buf2 = {7, 8};
 *
 * // create a file and write data to it
 * File file = new OpenOptions().create(true).open(repo, "/foo.txt");
 * file.writeOnce(buf);
 *
 * // read the first 2 bytes
 * byte[] dst = new byte[2];
 * file.seek(0, SeekFrom.START);
 * file.read(dst);  // dst: [1, 2]
 *
 * // create a new version, now the file content is [1, 2, 7, 8, 5, 6]
 * file.writeOnce(buf2);
 *
 * // notice that reading is on the latest version
 * file.seek(-2, SeekFrom.CURRENT);
 * file.read(dst);  // dst: [7, 8]
 *
 * fie.close();
 * </pre></blockquote>
 *
 * <p>Read multiple versions using {@link VersionReader}.</p>
 *
 * <blockquote><pre>
 * // create a file and write 2 versions
 * File file = new OpenOptions().create(true).versionLimit(4).open(repo, "/foo.txt");
 * file.writeOnce("foo".getBytes());
 * file.writeOnce("bar".getBytes());
 *
 * // get latest version number
 * long currVersion = file.currVersion();
 *
 * // create a version reader and read latest version of content
 * VersionReader vr = file.versionReader(currVersion);
 * ByteBuffer buf = vr.readAll();  // buf: "foobar"
 * vr.close();
 *
 * // create another version reader and read previous version of content
 * vr = file.versionReader(currVersion - 1);
 * buf = vr.readAll();  // buf: "foo"
 * vr.close();
 *
 * fie.close();
 * </pre></blockquote>
 *
 * @author Bo Lu
 * @see Repo
 * @see OpenOptions
 */
public class File extends RustObject {

    private static final int rustObjId = 103;

    // read buffer
    private static final int READ_BUF_CAP = 8 * 1024;
    private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUF_CAP);

    // write buffer, lazily allocated
    private static final int WRITE_BUF_CAP = 8 * 1024;
    private ByteBuffer writeBuf = null;

    private File() {
    }

    /**
     * Queries metadata about the file.
     *
     * @return file metadata
     * @throws ZboxException if any error happened
     */
    public Metadata metadata() throws ZboxException {
        return this.jniMetadata();
    }

    /**
     * Returns a list of all the file content versions.
     *
     * @return array of file content versions
     * @throws ZboxException if any error happened
     */
    public Version[] history() throws ZboxException {
        return this.jniHistory();
    }

    /**
     * Returns the current content version number.
     *
     * @return current content version number
     * @throws ZboxException if any error happened
     */
    public long currVersion() throws ZboxException {
        return this.jniCurrVersion();
    }

    /**
     * Get a reader of the specified version.
     *
     * <p>The returned reader is a {@link VersionReader}. To get the version number, first call
     * {@link #history()} to get the list of all versions and then choose the version number from
     * it.</p>
     *
     * @param verNum version number
     * @return a version reader
     * @throws ZboxException if any error happened
     */
    public VersionReader versionReader(long verNum) throws ZboxException {
        return this.jniVersionReader(verNum);
    }

    /**
     * Truncates or extends the underlying file, create a new version of content which size to
     * become {@code size}.
     *
     * <p>If the size is less than the current content size, then the new content will be shrunk.
     * If it is greater than the current content size, then the content will be extended to size
     * and have all of the intermediate data filled in with 0s.</p>
     *
     * @param len the new length to be set
     * @throws ZboxException if any error happened
     */
    public void setLen(long len) throws ZboxException {
        this.jniSetLen(len);
    }

    /**
     * Pull all bytes from this file into the specified byte buffer, returning how many bytes were
     * read.
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
     * @see #read(OutputStream)
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
     * Pull some bytes from this file into the specified byte array, returning how many bytes were
     * read.
     *
     * <p>This method copies {@code n} bytes from this file into the given destination array. If
     * there is no exception thrown, then it must be guaranteed that {@code 0 <= n <= len}.</p>
     *
     * <p>It is recommended to make {@code len} less than 8KB (8192) to avoid extra memory
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
     * Pull some bytes from this file into the specified byte array, returning how many bytes were
     * read.
     *
     * <p>This method copies bytes from this file into the given destination array. An invocation of
     * this method of the form {@code file.read(dst)} behaves in exactly the same way as the
     * invocation</p>
     *
     * <blockquote><pre>
     * file.read(dst, 0, dst.length)
     * </pre></blockquote>
     *
     * <p>It is recommended to make {@code dst.length} less than 8KB (8192) to avoid extra memory
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
     * Pull all bytes from this file into the specified output stream, returning how many bytes were
     * read.
     *
     * @param dst the output stream into which bytes are to be written
     * @return number of bytes were read
     * @throws ZboxException if any error happened
     * @see #read(ByteBuffer)
     * @see #readAll()
     */
    public long read(OutputStream dst) throws ZboxException {
        checkNullParam(dst);

        byte[] buf = new byte[8192];
        int read = 0;
        long total = 0;

        try {
            while ((read = this.read(buf)) > 0) {
                dst.write(buf, 0, read);
                total += read;
            }
        } catch (IOException err) {
            throw new ZboxException(err.toString());
        }

        return total;
    }

    /**
     * Read all bytes until end of the file, placing them into the returned buffer.
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
     * Read whole file content as a string until end of the file.
     *
     * @return the string holds all read bytes
     * @throws ZboxException if any error happened
     * @see #readAll()
     */
    public String readAllString() throws ZboxException {
        ByteBuffer buf = readAll();
        buf.flip();
        return StandardCharsets.UTF_8.decode(buf).toString();
    }

    /**
     * Write a buffer into this file, returning how many bytes were written.
     *
     * @param buf the source byte buffer from which bytes are to be read
     * @return number of bytes were written
     * @throws ZboxException if any error happened
     * @see #writeOnce(ByteBuffer)
     */
    public long write(ByteBuffer buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer src = this.ensureDirectBuf(buf);
        return this.jniWrite(src);
    }

    /**
     * Write some bytes from the specified byte array into this file, returning how many bytes were
     * written.
     *
     * <p>This method copies {@code n} bytes from the given source array into this file. If there is
     * no exception thrown, then it must be guaranteed that {@code 0 <= n <= len}.</p>
     *
     * <p>It is recommended to make {@code len} less than 8KB (8192) to avoid extra memory
     * allocation.</p>
     *
     * @param src the source array from which bytes are to be read
     * @param off the offset within the array of the first byte to be read; must be non-negative
     *            and no larger than dst.length
     * @param len the maximum number of bytes to be read from the given array; must be non-negative
     *            and no larger than dst.length - offset
     * @return number of bytes were written
     * @throws ZboxException if any error happened
     */
    public int write(byte[] src, int off, int len) throws ZboxException {
        checkNullParam(src);

        ByteBuffer buf;

        if (len > WRITE_BUF_CAP) {
            buf = ByteBuffer.allocateDirect(len);
        } else {
            if (this.writeBuf == null) this.writeBuf = ByteBuffer.allocateDirect(WRITE_BUF_CAP);

            buf = this.writeBuf.duplicate();
            buf.clear();
            buf.limit(len);
            buf = buf.slice();
        }

        buf.put(src, off, len);
        return (int) this.jniWrite(buf);
    }

    /**
     * Write some bytes from the specified byte array into this file, returning how many bytes were
     * written.
     *
     * <p>This method copies bytes from the given source array into this file. An invocation of
     * this method of the form {@code file.write(dst)} behaves in exactly the same way as the
     * invocation</p>
     *
     * <blockquote><pre>
     * file.write(src, 0, src.length)
     * </pre></blockquote>
     *
     * <p>It is recommended to make {@code src.length} less than 8KB (8192) to avoid extra memory
     * allocation.</p>
     *
     * @param src the source array from which bytes are to be read
     * @return number of bytes were written
     * @throws ZboxException if any error happened
     */
    public int write(byte[] src) throws ZboxException {
        return this.write(src, 0, src.length);
    }

    /**
     * Complete multi-part write to file and create a new version.
     *
     * @throws ZboxException if any error happened
     * @see #write(ByteBuffer)
     */
    public void finish() throws ZboxException {
        this.jniFinish();
    }

    /**
     * Single-part write to file and create a new version.
     *
     * <p>This method provides a convenient way to combine {@link #write(byte[])} and
     * {@link #finish()}.</p>
     *
     * <p>It is recommended to use a direct {@link java.nio.ByteBuffer} to avoid extra memory
     * allocation.</p>
     *
     * @param buf the source byte buffer from which bytes are to be read
     * @throws ZboxException if any error happened
     * @see #write(ByteBuffer)
     * @see #writeOnce(byte[])
     * @see #writeOnce(InputStream)
     */
    public void writeOnce(ByteBuffer buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer src = this.ensureDirectBuf(buf);
        this.jniWriteOnce(src);
    }

    /**
     * Single-part write to file and create a new version.
     *
     * <p>This method provides a convenient way to combine {@link #write(byte[])} and
     * {@link #finish()}.</p>
     *
     * @param buf the source byte array from which bytes are to be read
     * @throws ZboxException if any error happened
     * @see #write(byte[])
     * @see #writeOnce(ByteBuffer)
     * @see #writeOnce(InputStream)
     */
    public void writeOnce(byte[] buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer bytes = ByteBuffer.wrap(buf);
        bytes.position(buf.length);
        ByteBuffer src = this.ensureDirectBuf(bytes);
        this.jniWriteOnce(src);
    }

    /**
     * Single-part write string to file and create a new version.
     *
     * @param str the string from which bytes are to be read
     * @throws ZboxException if any error happened
     * @see #write(byte[])
     * @see #writeOnce(ByteBuffer)
     * @see #writeOnce(byte[])
     * @see #writeOnce(InputStream)
     */
    public void writeOnce(String str) throws ZboxException {
        checkNullParam(str);
        writeOnce(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Single-part write to file using an input stream and create a new version.
     *
     * @param stream the source input stream from which bytes are to be read
     * @throws ZboxException if any error happened
     * @see #write(byte[])
     * @see #writeOnce(ByteBuffer)
     * @see #writeOnce(byte[])
     */
    public void writeOnce(InputStream stream) throws ZboxException {
        checkNullParam(stream);

        byte[] buf = new byte[4096];
        int read;

        try {
            while((read = stream.read(buf)) > 0){
                this.write(buf, 0, read);
            }
        } catch (IOException err) {
            throw new ZboxException(err.toString());
        }

        this.finish();
    }

    /**
     * Seek to an offset, relative to from in bytes, in this file.
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
    private native Metadata jniMetadata() throws ZboxException;

    private native Version[] jniHistory() throws ZboxException;

    private native long jniCurrVersion() throws ZboxException;

    private native VersionReader jniVersionReader(long verNum) throws ZboxException;

    private native void jniFinish() throws ZboxException;

    private native void jniWriteOnce(ByteBuffer buf) throws ZboxException;

    private native void jniSetLen(long len) throws ZboxException;

    private native long jniRead(ByteBuffer dst) throws ZboxException;

    private native ByteBuffer jniReadAll() throws ZboxException;

    private native long jniWrite(ByteBuffer buf) throws ZboxException;

    private native long jniSeek(long offset, int whence) throws ZboxException;
}
