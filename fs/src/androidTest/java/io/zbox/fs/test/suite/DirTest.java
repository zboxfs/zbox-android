package io.zbox.fs.test.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.zbox.fs.DirEntry;
import io.zbox.fs.Repo;
import io.zbox.fs.RepoOpener;
import io.zbox.fs.ZboxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirTest {
    private static final String TAG = "DirTest";

    private Repo repo;

    @Before
    public void before() throws ZboxException {
        String uri = "file://" + TestSuite.testDir + "/" + TestSuite.randomString();
        this.repo = new RepoOpener().create(true).open(uri, "pwd");
    }

    @Test(expected = ZboxException.class)
    public void readNullDir() throws ZboxException {
        this.repo.readDir(null);
    }

    @Test(expected = ZboxException.class)
    public void readInvalidDir() throws ZboxException {
        this.repo.readDir("");
    }

    @Test(expected = ZboxException.class)
    public void readInvalidDir2() throws ZboxException {
        this.repo.readDir("non-exists");
    }

    @Test
    public void readRoot() throws ZboxException {
        DirEntry[] dirs = this.repo.readDir("/");
        assertEquals(dirs.length, 0);
    }

    @Test
    public void createDir() throws ZboxException {
        String path = "/dir01";

        // create empty dir
        this.repo.createDir(path);
        assertTrue(repo.isDir(path));
        assertFalse(repo.isFile(path));

        // read empty dir
        DirEntry[] dirs = this.repo.readDir(path);
        assertEquals(dirs.length, 0);

        // read root dir again
        dirs = this.repo.readDir("/");
        assertTrue(dirs.length > 0);
    }

    @Test
    public void createDirRecursively() throws ZboxException {
        String base = "/dir02";
        String dir1 = base + "/1";
        String path = dir1 + "/2";

        this.repo.createDirAll(path);
        assertTrue(repo.isDir(base));
        assertTrue(repo.isDir(dir1));
        assertTrue(repo.isDir(path));
        assertFalse(repo.isFile(path));

        DirEntry[] dirs = this.repo.readDir(base);
        assertEquals(dirs.length, 1);

        dirs = this.repo.readDir(dir1);
        assertEquals(dirs.length, 1);

        dirs = this.repo.readDir(path);
        assertEquals(dirs.length, 0);
    }

    @Test
    public void readDir() throws ZboxException {
        String base = "/dir03";
        String dir1 = base + "/1";
        String dir2 = base + "/2";

        this.repo.createDirAll(dir1);
        this.repo.createDirAll(dir2);

        DirEntry[] dirs = this.repo.readDir(base);
        assertEquals(dirs.length, 2);
        assertEquals(dirs[0].fileName, "1");
        assertEquals(dirs[0].path, dir1);
        assertEquals(dirs[1].fileName, "2");
        assertEquals(dirs[1].path, dir2);
    }

    @Test(expected = ZboxException.class)
    public void removeRootDir() throws ZboxException {
        this.repo.removeDir("/");
    }

    @Test(expected = ZboxException.class)
    public void removeNonExistDir() throws ZboxException {
        this.repo.removeDir("/non-exists");
    }

    @Test(expected = ZboxException.class)
    public void removeNonAbsDir() throws ZboxException {
        this.repo.removeDir("/1/2/3");
    }

    @Test(expected = ZboxException.class)
    public void removeNonEmptyDir() throws ZboxException {
        String path = "/dir04/1/2/3";
        this.repo.createDirAll(path);
        this.repo.removeDir("/dir04/1");
    }

    @Test
    public void removeEmptyDir() throws ZboxException {
        String path = "/dir05";
        this.repo.createDir(path);
        this.repo.removeDir("/dir05");
    }

    @Test
    public void removeNonEmptyDirRecursively() throws ZboxException {
        String path = "/dir06/1/2/3";
        this.repo.createDirAll(path);
        this.repo.removeDirAll("/dir06");
    }

    @Test(expected = ZboxException.class)
    public void readRemovedDir() throws ZboxException {
        String path = "/dir07";
        this.repo.createDir(path);
        this.repo.removeDir(path);
        this.repo.readDir(path);
    }

    @After
    public void after() {
        this.repo.close();
    }
}
