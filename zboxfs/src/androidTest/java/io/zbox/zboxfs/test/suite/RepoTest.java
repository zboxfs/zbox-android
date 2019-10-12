package io.zbox.zboxfs.test.suite;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

import io.zbox.zboxfs.Cipher;
import io.zbox.zboxfs.Env;
import io.zbox.zboxfs.File;
import io.zbox.zboxfs.FileType;
import io.zbox.zboxfs.MemLimit;
import io.zbox.zboxfs.Metadata;
import io.zbox.zboxfs.OpsLimit;
import io.zbox.zboxfs.Repo;
import io.zbox.zboxfs.RepoInfo;
import io.zbox.zboxfs.RepoOpener;
import io.zbox.zboxfs.Version;
import io.zbox.zboxfs.ZboxException;

public class RepoTest {
    private static final String TAG = "RepoTest";

    @Before
    public void before() {
    }

    @Test
    public void versionString() {
        String ver = Env.version();
        assertTrue(ver.matches("^ZboxFS.*"));
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg01() throws ZboxException {
        new RepoOpener().create(true).open("", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg02() throws ZboxException {
        new RepoOpener().create(true).open("wrong uri", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg03() throws ZboxException {
        new RepoOpener().create(true).open("wrong storage://", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg04() throws ZboxException {
        new RepoOpener().create(true).open("mem://", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg05() throws ZboxException {
        new RepoOpener().create(true).open("file://", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg06() throws ZboxException {
        new RepoOpener().create(true).open("zbox://", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg07() throws ZboxException {
        new RepoOpener().create(true).open("zbox://foo@", "");
    }

    @Test(expected = ZboxException.class)
    public void wrongOpenRepoArg08() throws ZboxException {
        new RepoOpener().create(true).open("zbox://foo@bar?cache_type=file", "");
    }

    @Test
    public void openMemRepoOk() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");
        repo.close();
    }

    @Test
    public void openNonExistRepo() throws ZboxException {
        assertFalse(Repo.exists("file://" + TestSuite.testDir + "/nonexist"));
    }

    @Test(expected = ZboxException.class)
    public void openRepoTwice() throws ZboxException {
        String uri = TestSuite.makeFileRepoUri();
        Repo repo = new RepoOpener().create(true).open(uri, "pwd");
        Repo repo2 = new RepoOpener().create(true).open(uri, "pwd");
        repo.close();
        repo2.close();
    }

    @Test
    public void forceOpenRepo() throws ZboxException {
        String uri = TestSuite.makeFileRepoUri();
        Repo repo = new RepoOpener().create(true).open(uri, "pwd");
        Repo repo2 = new RepoOpener().create(true).force(true).open(uri, "pwd");
        repo.close();
        repo2.close();
    }

    @Test
    public void repoInfo() throws ZboxException {
        String uri = TestSuite.makeMemRepoUri();
        Repo repo = new RepoOpener().create(true).open(uri, "pwd");
        RepoInfo info = repo.info();
        assertEquals(info.volumeId.length, 32);
        assertTrue(info.version.length() > 0);
        assertEquals(info.uri, uri);
        assertEquals(info.opsLimit, OpsLimit.INTERACTIVE);
        assertEquals(info.memLimit, MemLimit.INTERACTIVE);
        assertEquals(info.cipher, Cipher.AES);
        assertFalse(info.compress);
        assertEquals(info.versionLimit, 1);
        assertFalse(info.dedupChunk);
        assertFalse(info.isReadOnly);
        assertTrue(info.createdAt > 0);
        repo.close();
    }

    @Test
    public void resetRepoPassword() throws ZboxException {
        String uri = "file://" + TestSuite.testDir + "/" + TestSuite.randomString();
        String pwd = "pwd", newPwd = "new-pwd";
        RepoOpener opener = new RepoOpener().create(true);

        Repo repo = opener.open(uri, pwd);
        repo.resetPassword(pwd, newPwd, OpsLimit.INTERACTIVE, MemLimit.INTERACTIVE);
        repo.close();

        // open repo again with new password should success
        repo = opener.open(uri, newPwd);
        repo.close();
    }

    @Test(expected = ZboxException.class)
    public void resetRepoPassword2() throws ZboxException {
        String uri = "file://" + TestSuite.testDir + "/" + TestSuite.randomString();
        String pwd = "pwd", newPwd = "new-pwd";
        RepoOpener opener = new RepoOpener().create(true);

        // open repo with specified ops_limit and then change password
        Repo repo = opener.open(uri, pwd);
        repo.resetPassword("wrong pwd", newPwd, OpsLimit.INTERACTIVE, MemLimit.INTERACTIVE);
        repo.close();
    }

    @Test(expected = ZboxException.class)
    public void openRepoAfterResetPwd() throws ZboxException {
        String uri = "file://" + TestSuite.testDir + "/" + TestSuite.randomString();
        String pwd = "pwd", newPwd = "new-pwd";
        RepoOpener opener = new RepoOpener().create(true);

        Repo repo = opener.open(uri, pwd);
        repo.resetPassword(pwd, newPwd, OpsLimit.INTERACTIVE, MemLimit.INTERACTIVE);
        repo.close();

        // open repo again with old password should fail
        repo = opener.open(uri, pwd);
        repo.close();
    }

    @Test
    public void checkPathExists() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");
        assertTrue(repo.pathExists("/"));
        assertFalse(repo.pathExists("/non-exists"));
        repo.createDirAll("/1/2/3");
        assertTrue(repo.pathExists("/1/2/3"));
        repo.close();
    }

    @Test(expected = ZboxException.class)
    public void checkPathExists2() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");
        repo.pathExists(null);
        repo.close();
    }

    @Test
    public void createFile() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");
        File file = repo.createFile("/abc");
        assertNotNull(file);
        file.close();
        repo.close();
    }

    @Test
    public void getMetadataFromPath() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");

        File file = repo.createFile("/abc");
        ByteBuffer buf = ByteBuffer.allocateDirect(20);
        buf.put((byte) 1);
        buf.put((byte) 2);
        buf.put((byte) 3);
        file.writeOnce(buf);
        file.close();

        Metadata md = repo.metadata("/abc");
        assertNotNull(md);
        assertEquals(md.fileType, FileType.FILE);
        assertTrue(md.isFile());
        assertFalse(md.isDir());
        assertEquals(md.contentLen, 3);
        assertEquals(md.currVersion, 2);
        assertTrue(md.createdAt > 0);
        assertTrue(md.modifiedAt > 0);

        repo.close();
    }

    @Test
    public void getHistoryFromPath() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");

        File file = repo.createFile("/abc");
        ByteBuffer buf = ByteBuffer.allocateDirect(20);
        buf.put((byte) 1);
        buf.put((byte) 2);
        buf.put((byte) 3);
        file.writeOnce(buf);
        file.close();

        Version[] hist = repo.history("/abc");
        assertNotNull(hist);
        assertEquals(hist.length, 1);
        assertEquals(hist[0].num, 2);
        assertEquals(hist[0].contentLen, 3);
        assertTrue(hist[0].createdAt > 0);

        repo.close();
    }

    @Test
    public void copyFile() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");

        File file = repo.createFile("/src");
        ByteBuffer buf = ByteBuffer.allocateDirect(20);
        buf.put((byte) 1);
        buf.put((byte) 2);
        buf.put((byte) 3);
        file.writeOnce(buf);
        file.close();

        repo.copy("/src", "/tgt");
        assertTrue(repo.isFile("/tgt"));
        Metadata md = repo.metadata("/tgt");
        assertNotNull(md);
        assertEquals(md.fileType, FileType.FILE);
        assertTrue(md.isFile());
        assertFalse(md.isDir());
        assertEquals(md.contentLen, 3);
        assertEquals(md.currVersion, 2);
        assertTrue(md.createdAt > 0);
        assertTrue(md.modifiedAt > 0);

        // copy file to itself should success
        repo.copy("/src", "/src");

        repo.close();
    }

    @Test
    public void removeFile() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");

        String path = "/file";
        File file = repo.createFile(path);
        file.close();

        assertTrue(repo.pathExists(path));
        repo.removeFile(path);
        assertFalse(repo.pathExists(path));
    }

    @Test(expected = ZboxException.class)
    public void removeFile2() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");

        String path = "/file";
        File file = repo.createFile(path);
        file.close();

        repo.removeFile(path);
        repo.removeFile(path); // should fail
    }

    @Test
    public void renameFile() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(TestSuite.makeMemRepoUri(), "pwd");

        String path = "/file";
        File file = repo.createFile(path);
        file.close();

        assertTrue(repo.pathExists(path));
        repo.rename(path, "/new-name");
        assertFalse(repo.pathExists(path));
        assertTrue(repo.pathExists("/new-name"));
    }

    @Test
    public void repairSuperBlock() throws ZboxException {
        String uri = TestSuite.makeFileRepoUri();
        String pwd = "pwd";
        Repo repo = new RepoOpener().create(true).open(uri, pwd);
        repo.close();
        Repo.repairSuperBlock(uri, pwd);
    }

}
