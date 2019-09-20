package io.zbox.fs.test.suite;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import io.zbox.fs.File;
import io.zbox.fs.OpenOptions;
import io.zbox.fs.Repo;
import io.zbox.fs.RepoOpener;
import io.zbox.fs.ZboxException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileTest {

    private static final String TAG = "FileTest";

    private String uri;
    private Repo repo;
    private ByteBuffer buf, buf2, dst;

    private void assertBufEquals(ByteBuffer dst, ByteBuffer src) {
        ByteBuffer buf = dst.asReadOnlyBuffer();
        buf.flip();
        ByteBuffer buf2 = src.asReadOnlyBuffer();
        buf2.flip();
        assertEquals(buf, buf2);
    }

    @Before
    public void before() throws ZboxException {
        this.uri = "file://" + TestSuite.testDir + "/" + TestSuite.randomString();
        this.repo = new RepoOpener().create(true).open(this.uri, "pwd");

        this.buf = ByteBuffer.allocateDirect(20);
        buf.put((byte)1);
        buf.put((byte)2);
        buf.put((byte)3);
        this.buf = this.buf.asReadOnlyBuffer();
        this.buf2 = ByteBuffer.allocateDirect(20);
        buf2.put((byte)4);
        buf2.put((byte)5);
        buf2.put((byte)6);
        buf2.put((byte)7);
        this.buf2 = this.buf2.asReadOnlyBuffer();
        this.dst = ByteBuffer.allocateDirect(20);
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenFileArg01() throws ZboxException {
        new OpenOptions().create(true).open(null, "/foo");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenFileArg02() throws ZboxException {
        new OpenOptions().create(true).open(this.repo, null);
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenFileArg03() throws ZboxException {
        new OpenOptions().create(true).open(null, null);
    }

    @Test
    public void emptyFileIO() throws ZboxException {
        String path = "/file01";
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.close();

        this.dst.clear();
        file = repo.openFile(path);
        long read = file.read(dst);
        file.close();
        assertEquals(read, 0);
    }

    @Test
    public void singleWrite() throws ZboxException {
        String path = "/file02";
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.writeOnce(this.buf);
        file.close();

        this.dst.clear();
        file = repo.openFile(path);
        long read = file.read(dst);
        file.close();

        assertEquals(read, this.buf.position());
        assertBufEquals(this.dst, this.buf);
    }

    @Test
    public void multipleWrite() throws ZboxException {
        String path = "/file03";
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.write(this.buf);
        file.write(this.buf2);
        file.finish();
        file.close();

        this.dst.clear();
        file = repo.openFile(path);
        long read = file.read(dst);
        file.close();

        assertEquals(read, this.buf.position() + this.buf2.position());
        ByteBuffer src = ByteBuffer.allocate(this.buf.capacity() + this.buf2.capacity());
        src.put((ByteBuffer)this.buf.asReadOnlyBuffer().flip());
        src.put((ByteBuffer)this.buf2.asReadOnlyBuffer().flip());
        assertBufEquals(this.dst, src);
    }

    @Test
    public void singleRead() throws ZboxException {
        String path = "/file04";
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.writeOnce(this.buf);
        file.close();

        file = repo.openFile(path);
        ByteBuffer dst = file.readAll();
        assertEquals(dst.position(), this.buf.position());
        assertBufEquals(dst, this.buf);

        file.close();
    }

    @Test
    public void multipleRead() throws ZboxException {
        String path = "/file05";
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.write(this.buf);
        file.write(this.buf2);
        file.finish();
        file.close();

        file = repo.openFile(path);

        // read part #1
        this.dst.clear();
        this.dst.limit(this.buf.position());
        long read = file.read(dst);
        assertEquals(read, this.buf.position());
        assertBufEquals(this.dst, this.buf);

        // read part #2
        this.dst.clear();
        this.dst.limit(this.buf2.position());
        read = file.read(dst);
        assertEquals(read, this.buf2.position());
        assertBufEquals(this.dst, this.buf2);

        file.close();
    }

    @Test
    public void byteArrayRead() throws ZboxException {
        String path = "/file06";
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.writeOnce(this.buf);
        file.close();

        file = repo.openFile(path);

        byte[] dst = new byte[this.buf.capacity()];
        int read = file.read(dst);
        assertEquals(read, this.buf.position());
        byte[] bytes = new byte[this.buf.capacity()];
        ByteBuffer buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.get(bytes,0, read);
        assertArrayEquals(bytes, dst);
    }

    @After
    public void after() {
        this.repo.close();
    }
}
