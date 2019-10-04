package io.zbox.fs;

import java.nio.ByteBuffer;

/**
 * A reference to an opened file in the repository.
 *
 * <p>An instance of a {@code File} can be read and/or written depending on what options it was
 * opened with. {@code File} also implements {@link io.zbox.fs.SeekFrom} to alter the logical cursor
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
 * {@link Version} number starts from <b>1</b> and continuously increases by <b>1</b>.</p>
 *
 * <h3>Writing</h3>
 *
 * <p>{@code File} is multi-versioned, each time updating its content will create a new permanent
 * {@link Version}. There are two ways of writing data to a file:</p>
 *
 * <ul>
 *     <li>
 *         <h4>Multi-part Write</h4>
 *         <p>This is done by updating {@code File} using {@link #write(byte[])} method multiple
 *         times. After all writing operations, {@link #finish()} must be called to create a new
 *         {@link Version}.</p>
 *         <h5>Examples</h5>
 *         <blockquote><pre>
 *  File file = new OpenOptions().create(true).open(repo, "/foo.txt");
 *  long written = file.write("My text".getBytes());
 *  written = file.write("My text2".getBytes());
 *  file.finish();
 *         </pre></blockquote>
 *     </li>
 *     <li>
 *         <h4>Single-part Write</h4>
 *         <p>This can be done by calling {@link #writeOnce(ByteBuffer)}, which will call
 *         {@link #finish()} internally to create a new {@link Version}.</p>
 *         <h5>Examples</h5>
 *         <blockquote><pre>
 *  File file = new OpenOptions().create(true).open(repo, "/foo.txt");
 *  file.writeOnce("My text".getBytes());
 *         </pre></blockquote>
 *     </li>
 * </ul>
 *
 * <h3>Reading</h3>
 *
 * <p>As {@code File} can contain multiple versions, read operation can be associated with different
 * versions. By default, reading on {@code File} object is always bound to the latest version. To
 * read a specific version, a {@link VersionReader}, which supports
 * {@link io.zbox.fs.VersionReader#read(byte[])} as well, can be used.</p>
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
 * </pre></blockquote>
 *
 * @author Bo Lu
 * @see io.zbox.fs.Repo
 */
public class File extends RustObject {

    private static int rustObjId = 103;

    // read buffer
    private static final int READ_BUF_CAP = 16 * 1024;
    private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUF_CAP);

    // write buffer, lazily allocated
    private static final int WRITE_BUF_CAP = 16 * 1024;
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

    public VersionReader versionReader(long verNum) throws ZboxException {
        return this.jniVersionReader(verNum);
    }

    public void setLen(long len) throws ZboxException {
        this.jniSetLen(len);
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

    public long write(ByteBuffer buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer src = this.ensureDirectBuf(buf);
        return this.jniWrite(src);
    }

    public int write(byte[] src, int off, int len) throws ZboxException {
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

    public int write(byte[] src) throws ZboxException {
        return this.write(src, 0, src.length);
    }

    public void finish() throws ZboxException {
        this.jniFinish();
    }

    public void writeOnce(ByteBuffer buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer src = this.ensureDirectBuf(buf);
        this.jniWriteOnce(src);
    }

    public void writeOnce(byte[] buf) throws ZboxException {
        checkNullParam(buf);
        ByteBuffer bytes = ByteBuffer.wrap(buf);
        bytes.position(buf.length);
        ByteBuffer src = this.ensureDirectBuf(bytes);
        this.jniWriteOnce(src);
    }

    public long seek(long offset, SeekFrom whence) throws ZboxException {
        checkNullParam(whence);
        return this.jniSeek(offset, whence.getValue());
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
