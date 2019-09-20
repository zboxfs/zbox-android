package io.zbox.fs.test.suite;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import io.zbox.fs.Env;
import io.zbox.fs.Repo;
import io.zbox.fs.RepoOpener;
import io.zbox.fs.ZboxException;

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
        Repo repo = new RepoOpener().create(true).open("mem://foo", "pwd");
        repo.close();
    }

    @Test
    public void openNonExistRepo() throws ZboxException {
        assertFalse(Repo.exists("file://" + TestSuite.testDir + "/nonexist"));
    }
}
