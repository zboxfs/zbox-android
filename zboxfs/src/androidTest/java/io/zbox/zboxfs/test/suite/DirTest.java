package io.zbox.zboxfs.test.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.zbox.zboxfs.DirEntry;
import io.zbox.zboxfs.Path;
import io.zbox.zboxfs.Repo;
import io.zbox.zboxfs.RepoOpener;
import io.zbox.zboxfs.ZboxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DirTest {
    private static final String TAG = "DirTest";

    private Repo repo;

    @Before
    public void before() throws ZboxException {
        String uri = TestSuite.makeFileRepoUri();
        this.repo = new RepoOpener().create(true).open(uri, "pwd");
    }

    @Test(expected = ZboxException.class)
    public void readNullDir() throws ZboxException {
        this.repo.readDir(null);
    }

    @Test(expected = ZboxException.class)
    public void readInvalidDir() throws ZboxException {
        this.repo.readDir(new Path(""));
    }

    @Test(expected = ZboxException.class)
    public void readInvalidDir2() throws ZboxException {
        this.repo.readDir(new Path("non-exists"));
    }

    @Test
    public void readRoot() throws ZboxException {
        DirEntry[] dirs = this.repo.readDir(new Path("/"));
        assertEquals(dirs.length, 0);
    }

    @Test
    public void createDir() throws ZboxException {
        Path path = new Path("/dir01");

        // create empty dir
        this.repo.createDir(path);
        assertTrue(repo.isDir(path));
        assertFalse(repo.isFile(path));

        // read empty dir
        DirEntry[] dirs = this.repo.readDir(path);
        assertEquals(dirs.length, 0);

        // read root dir again
        dirs = this.repo.readDir(new Path("/"));
        assertTrue(dirs.length > 0);
    }

    @Test
    public void createDirRecursively() throws ZboxException {
        Path base = new Path("/dir02");
        Path dir1 = base.join("1");
        Path path = dir1.join("2");

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
        Path base = new Path("/dir03");
        Path dir1 = base.join("1");
        Path dir2 = base.join("2");

        this.repo.createDirAll(dir1);
        this.repo.createDirAll(dir2);

        DirEntry[] dirs = this.repo.readDir(base);
        assertEquals(dirs.length, 2);
        assertEquals(dirs[0].fileName, "1");
        assertEquals(dirs[0].path, dir1);
        assertNotNull(dirs[0].metadata);
        assertEquals(dirs[1].fileName, "2");
        assertEquals(dirs[1].path, dir2);
    }

    @Test(expected = ZboxException.class)
    public void removeRootDir() throws ZboxException {
        this.repo.removeDir(Path.root());
    }

    @Test(expected = ZboxException.class)
    public void removeNonExistDir() throws ZboxException {
        this.repo.removeDir(new Path("/non-exists"));
    }

    @Test(expected = ZboxException.class)
    public void removeNonAbsDir() throws ZboxException {
        this.repo.removeDir(new Path("/1/2/3"));
    }

    @Test(expected = ZboxException.class)
    public void removeNonEmptyDir() throws ZboxException {
        Path path = new Path("/dir04/1/2/3");
        this.repo.createDirAll(path);
        this.repo.removeDir(new Path("/dir04/1"));
    }

    @Test
    public void removeEmptyDir() throws ZboxException {
        Path path = new Path("/dir05");
        this.repo.createDir(path);
        this.repo.removeDir(path);
    }

    @Test
    public void removeNonEmptyDirRecursively() throws ZboxException {
        Path path = new Path("/dir06/1/2/3");
        this.repo.createDirAll(path);
        this.repo.removeDirAll(new Path("/dir06"));
    }

    @Test(expected = ZboxException.class)
    public void readRemovedDir() throws ZboxException {
        Path path = new Path("/dir07");
        this.repo.createDir(path);
        this.repo.removeDir(path);
        this.repo.readDir(path);
    }

    @After
    public void after() {
        this.repo.close();
    }
}
