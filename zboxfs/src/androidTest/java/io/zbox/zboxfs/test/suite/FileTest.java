package io.zbox.zboxfs.test.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import io.zbox.zboxfs.File;
import io.zbox.zboxfs.Metadata;
import io.zbox.zboxfs.OpenOptions;
import io.zbox.zboxfs.Path;
import io.zbox.zboxfs.Repo;
import io.zbox.zboxfs.RepoOpener;
import io.zbox.zboxfs.SeekFrom;
import io.zbox.zboxfs.Version;
import io.zbox.zboxfs.VersionReader;
import io.zbox.zboxfs.ZboxException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FileTest {

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
        String uri = TestSuite.makeFileRepoUri();
        this.repo = new RepoOpener().create(true).open(uri, "pwd");

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
        new OpenOptions().create(true).open(null, new Path("/foo"));
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
        Path path = new Path("/file01");
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
        Path path = new Path("/file02");
        File file = new OpenOptions().create(true).dedupChunk(false).open(this.repo, path);
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
        Path path = new Path("/file03");
        File file = new OpenOptions().create(true).open(this.repo, path);
        long written = file.write(this.buf);
        assertEquals(written, this.buf.position());
        written = file.write(this.buf2);
        assertEquals(written, this.buf2.position());
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
        Path path = new Path("/file04");
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
        Path path = new Path("/file05");
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
        Path path = new Path("/file06");
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.writeOnce(this.buf);
        file.close();

        // single read all
        file = repo.openFile(path);
        byte[] dst = new byte[this.buf.capacity()];
        int read = file.read(dst);
        assertEquals(read, this.buf.position());
        byte[] bytes = new byte[this.buf.capacity()];
        ByteBuffer buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.get(bytes,0, read);
        assertArrayEquals(dst, bytes);
        file.close();

        // multiple read, #1
        file = repo.openFile(path);
        dst = new byte[2];
        read = file.read(dst);
        assertEquals(read, dst.length);
        bytes = new byte[dst.length];
        buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.get(bytes,0, read);
        assertArrayEquals(dst, bytes);

        // multiple read, #2
        int left = this.buf.position() - read;
        read = file.read(dst);
        assertEquals(read, left);
        buf.get(bytes,0, read);
        assertArrayEquals(dst, bytes);
        file.close();

        // read from offset
        file = repo.openFile(path);
        dst = new byte[3];
        read = file.read(dst, 1,2);
        assertEquals(read, 2);
        bytes = new byte[dst.length];
        buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.get(bytes,1, read);
        assertArrayEquals(dst, bytes);
        file.close();

        // test file seek
        file = repo.openFile(path);
        long sought = file.seek(1, SeekFrom.START);
        assertEquals(sought, 1);
        dst = new byte[2];
        file.read(dst);
        buf = this.buf.asReadOnlyBuffer();
        buf.rewind();
        buf.get();
        assertEquals(dst[0], buf.get());
        assertEquals(dst[1], buf.get());
        file.close();
    }

    @Test
    public void byteArrayWrite() throws ZboxException {
        Path path = new Path("/file07");
        File file = new OpenOptions().create(true).open(this.repo, path);

        // write to file with whole byte array
        byte[] src = new byte[this.buf.position()];
        ByteBuffer buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.get(src);
        int written = file.write(src);
        file.finish();
        file.close();
        assertEquals(written, this.buf.position());

        // verify file content
        file = repo.openFile(path);
        ByteBuffer dst = file.readAll();
        assertBufEquals(dst, this.buf);
        file.close();

        // write to file with partial byte array
        path = new Path("/file07-1");
        file = new OpenOptions().create(true).open(this.repo, path);
        written = file.write(src, 1, 2);
        file.finish();
        file.close();
        assertEquals(written, 2);

        // verify file content
        file = repo.openFile(path);
        dst = file.readAll();
        dst.rewind();
        buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf = buf.slice();
        assertBufEquals(dst, buf);
    }

    @Test
    public void inputStreamWrite() throws ZboxException {
        Path path = new Path("/file08");
        File file = new OpenOptions().create(true).open(this.repo, path);

        // make input stream
        byte[] src = new byte[this.buf.position()];
        ByteBuffer buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.get(src);
        ByteArrayInputStream stream = new ByteArrayInputStream(src);

        // write to file
        file.writeOnce(stream);
        file.close();

        // verify file content
        file = repo.openFile(path);
        dst = file.readAll();
        dst.rewind();
        buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        buf.position(1);
        buf = buf.slice();
        assertBufEquals(dst, buf);
    }

    @Test
    public void metadata() throws ZboxException {
        Path path = new Path("/file09");
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.writeOnce(this.buf);
        file.close();

        file = repo.openFile(path);
        Metadata md = file.metadata();
        assertNotNull(md);
        assertTrue(md.isFile());
        assertFalse(md.isDir());
        assertEquals(md.contentLen, this.buf.position());
        assertEquals(md.currVersion, 2);
        assertTrue(md.createdAt > 0);
        assertTrue(md.modifiedAt > 0);
        file.close();
    }

    @Test
    public void versioning() throws ZboxException {
        Path path = new Path("/file10");
        File file = new OpenOptions().create(true).versionLimit(2).open(this.repo, path);

        // write version #1
        file.writeOnce(this.buf);
        file.close();

        // write version #2
        file = new OpenOptions().write(true).open(this.repo, path);
        file.writeOnce(this.buf2);
        file.close();

        file = repo.openFile(path);
        long ver = file.currVersion();
        assertEquals(ver, 3);
        Version[] hist = file.history();
        assertEquals(hist.length, 2);
        assertEquals(hist[0].num, 2);
        assertEquals(hist[0].contentLen, this.buf.position());
        assertEquals(hist[1].num, 3);
        assertEquals(hist[1].contentLen, this.buf2.position());

        // test version reader #1
        VersionReader vr = file.versionReader(2);
        ByteBuffer buf = vr.readAll();
        assertBufEquals(buf, this.buf);
        vr.close();

        // test version reader seek
        vr = file.versionReader(2);
        long sought = vr.seek(1, SeekFrom.START);
        assertEquals(sought, 1);
        buf = vr.readAll();
        ByteBuffer dst = this.buf.asReadOnlyBuffer();
        dst.flip();
        dst.position(1);
        dst = dst.slice();
        dst.position(2);
        assertBufEquals(dst, buf);
        vr.close();

        // test version reader #2
        vr = file.versionReader(3);
        dst = ByteBuffer.allocateDirect(this.buf2.position());
        long read = vr.read(dst);
        assertEquals(read, this.buf2.position());
        assertBufEquals(dst, this.buf2);
        vr.close();

        // test version reader read to byte array
        vr = file.versionReader(3);
        byte[] dst2 = new byte[3];
        read = (long)vr.read(dst2);
        assertEquals(read, dst2.length);
        buf = this.buf2.asReadOnlyBuffer();
        buf.rewind();
        assertEquals(dst2[0], buf.get());
        assertEquals(dst2[1], buf.get());
        assertEquals(dst2[2], buf.get());
        vr.close();

        // test version reader read to byte array partially
        vr = file.versionReader(3);
        byte[] dst3 = new byte[3];
        read = (long)vr.read(dst3, 1, 2);
        assertEquals(read, dst3.length - 1);
        buf = this.buf2.asReadOnlyBuffer();
        buf.rewind();
        assertEquals(dst3[1], buf.get());
        assertEquals(dst3[2], buf.get());
        vr.close();

        file.close();
    }

    @Test
    public void setNewLen() throws ZboxException {
        Path path = new Path("/file11");
        File file = new OpenOptions().create(true).open(this.repo, path);
        file.writeOnce(this.buf);
        file.close();

        // case #1: extend file
        file = new OpenOptions().write(true).open(this.repo, path);
        file.setLen(this.buf.position() + 2);
        file.close();

        // verify file content
        file = repo.openFile(path);
        ByteBuffer dst = file.readAll();
        ByteBuffer src = ByteBuffer.allocate(this.buf.position() + 2);
        ByteBuffer buf = this.buf.asReadOnlyBuffer();
        buf.flip();
        src.put(buf);
        src.put((byte)0);
        src.put((byte)0);
        assertBufEquals(dst, src);
        file.close();

        // case #2: truncate file
        file = new OpenOptions().write(true).open(this.repo, path);
        file.setLen(this.buf.position() - 2);
        file.close();

        // verify file content
        file = repo.openFile(path);
        dst = file.readAll();
        assertEquals(dst.position(), 1);
        buf = this.buf.asReadOnlyBuffer();
        buf.rewind();
        dst.rewind();
        assertEquals(dst.get(), buf.get());
        file.close();
    }

    @Test
    public void exampleCodeInDoc() throws ZboxException {
        Path path = new Path("/file12");
        byte[] buf = {1, 2, 3, 4, 5, 6};
        byte[] buf2 = {7, 8};

        // create a file and write data to it
        File file = new OpenOptions().create(true).open(repo, path);
        file.writeOnce(buf);

        // read the first 2 bytes
        byte[] dst = new byte[2];
        file.seek(0, SeekFrom.START);
        assertEquals(file.read(dst), 2);  // dst: [1, 2]
        assertEquals(dst[0], 1);
        assertEquals(dst[1], 2);

        // create a new version, now the file content is [1, 2, 7, 8, 5, 6]
        file.writeOnce(buf2);

        // notice that reading is on the latest version
        file.seek(-2, SeekFrom.CURRENT);
        assertEquals(file.read(dst), 2);  // dst: [7, 8]
        assertEquals(dst[0], 7);
        assertEquals(dst[1], 8);

        file.close();
    }

    @Test
    public void exampleCodeInDoc2() throws ZboxException {
        Path path = new Path("/file13");

        // create a file and write 2 versions
        File file = new OpenOptions().create(true).versionLimit(4).open(repo, path);
        file.writeOnce("foo".getBytes());
        file.writeOnce("bar".getBytes());

        // get latest version number
        long currVersion = file.currVersion();

        // create a version reader and read latest version of content
        VersionReader vr = file.versionReader(currVersion);
        ByteBuffer buf = vr.readAll();  // buf: "foobar"
        vr.close();

        // create another version reader and read previous version of content
        vr = file.versionReader(currVersion - 1);
        buf = vr.readAll();  // buf: "foo"
        vr.close();

        file.close();
    }

    @After
    public void after() {
        this.repo.close();
    }
}
