package io.zbox.zboxfs;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>A {@code FileInputStream} obtains input bytes from a file in a ZboxFS file system.</p>
 *
 * <p>To obtain a {@code FileInputStream} instance, use
 * {@link io.zbox.zboxfs.Repo#openFileInputStream(Path)}.</p>
 */
public class FileInputStream extends InputStream {

    private final File file;
    private long read = 0;

    FileInputStream(File file) {
        this.file = file;
    }

    /**
     * <p>Returns an estimate of the number of remaining bytes that can be read (or skipped over)
     * from this input stream without blocking by the next invocation of a method for this input
     * stream.</p>
     *
     * @return an estimate of the number of bytes that can be read (or skipped over) from this input
     *         stream without blocking or 0 when it reaches the end of the input stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
        Metadata md;

        try {
            md = file.metadata();
        } catch (ZboxException err) {
            throw new IOException(err.toString());
        }

        return (int)(md.contentLen - read);
    }

    /**
     * <p>Closes this input stream and releases the file associated with the stream.</p>
     */
    @Override
    public void close() {
        file.close();
    }

    /**
     * <p>Reads the next byte of data from the stream. The value byte is returned as an int in
     * the range 0 to 255. If no byte is available because the end of the stream has been reached,
     * the value -1 is returned. This method blocks until input data is available, the end of the
     * stream is detected, or an exception is thrown.</p>
     *
     * @return the next byte of data, or -1 if the end of the stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        byte[] buf = new byte[1];
        int read;

        try {
            read = file.read(buf);
            this.read += read;
            if (read == 0) read = -1;
        } catch (ZboxException err) {
            throw new IOException(err.toString());
        }

        return read;
    }

    /**
     * <p>Reads up to {@code len} bytes of data from the input stream into an array of bytes. An
     * attempt is made to read as many as {@code len} bytes, but a smaller number may be read. The
     * number of bytes actually read is returned as an integer.</p>
     *
     * <p>This method blocks until input data is available, end of file is detected, or an exception
     * is thrown.</p>
     *
     * <p>If {@code len} is zero, then no bytes are read and {@code 0} is returned; otherwise, there
     * is an attempt to read at least one byte. If no byte is available because the stream is at end
     * of file, the value {@code -1} is returned; otherwise, at least one byte is read and stored
     * into {@code b}.</p>
     *
     * <p>The first byte read is stored into element {@code b[off]}, the next one into
     * {@code b[off+1]}, and so on. The number of bytes read is, at most, equal to {@code len}.
     * Let {@code k} be the number of bytes actually read; these bytes will be stored in elements
     * {@code b[off]} through {@code b[off+k-1]}, leaving elements {@code b[off+k]} through
     * {@code b[off+len-1]} unaffected.</p>
     *
     * <p>In every case, elements {@code b[0]} through {@code b[off]} and elements {@code b[off+len]}
     * through {@code b[b.length-1]} are unaffected.</p>
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array {@code b} at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or -1 if there is no more data
     *         because the end of the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read;

        try {
            read = file.read(b, off, len);
            this.read += read;
            if (read == 0 && len > 0) read = -1;
        } catch (ZboxException err) {
            throw new IOException(err.toString());
        }

        return read;
    }

    /**
     * <p>Reads some number of bytes from the stream and stores them into the buffer array {@code b}.
     * The number of bytes actually read is returned as an integer. This method blocks until input
     * data is available, end of file is detected, or an exception is thrown.</p>
     *
     * <p>If the length of {@code b} is zero, then no bytes are read and {@code 0} is returned;
     * otherwise, there is an attempt to read at least one byte. If no byte is available because the
     * stream is at the  end of the file, the value {@code -1} is returned; otherwise, at least one
     * byte is read and stored into {@code b}.</p>
     *
     * <p>The first byte read is stored into element {@code b[0]}, the next one into {@code b[1]},
     * and so on. The number of bytes read is, at most, equal to the length of {@code b}. Let
     * {@code k} be the number of bytes actually read; these bytes will be stored in elements
     * {@code b[0]} through {@code b[k-1]}, leaving elements {@code b[k]} through
     * {@code b[b.length-1]} unaffected.</p>
     *
     * <p>The {@code read(b)} method has the same effect as: </p>
     *
     * <blockquote><pre>
     *  read(b, 0, b.length)
     * </pre></blockquote>
     *
     * @param b the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or -1 if there is no more data
     *         because the end of the stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(byte[] b) throws IOException {
        int read;

        try {
            read = file.read(b);
            this.read += read;
            if (read == 0 && b.length > 0) read = -1;
        } catch (ZboxException err) {
            throw new IOException(err.toString());
        }

        return read;
    }

    /**
     * <p>Skips over and discards {@code n} bytes of data from this stream. The skip method may, for
     * a variety of reasons, end up skipping over some smaller number of bytes, possibly {@code 0}.
     * This may result from any of a number of conditions; reaching end of file before {@code n}
     * bytes have been skipped is only one possibility. The actual number of bytes skipped is
     * returned. If {@code n} is negative, no bytes are skipped.</p>
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long skip(long n) throws IOException {
        if (n < 0) return 0;

        long skipped;

        try {
            long currPos = file.seek(0, SeekFrom.CURRENT);
            long newPos = file.seek(n, SeekFrom.CURRENT);
            skipped = newPos - currPos;
        } catch (ZboxException err) {
            throw new IOException(err.toString());
        }

        return skipped;
    }
}
