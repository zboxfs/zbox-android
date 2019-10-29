package io.zbox.zboxfs;

import java.io.IOException;
import java.io.InputStream;

public class FileInputStream extends InputStream {

    private final File file;
    private long read = 0;

    public FileInputStream(File file) {
        this.file = file;
    }

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

    @Override
    public void close () {
        file.close();
    }

    @Override
    public int read () throws IOException {
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

    @Override
    public int read (byte[] b, int off, int len) throws IOException {
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

    @Override
    public int read (byte[] b) throws IOException {
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

    @Override
    public long skip (long n) throws IOException {
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
