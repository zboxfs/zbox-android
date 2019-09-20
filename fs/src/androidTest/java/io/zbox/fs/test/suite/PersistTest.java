package io.zbox.fs.test.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import io.zbox.fs.Repo;
import io.zbox.fs.RepoOpener;
import io.zbox.fs.ZboxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PersistTest {

    private static final String TAG = "PersistTest";

    private String uri;
    private Repo repo;

    @Before
    public void before() throws ZboxException {
        this.uri = "file://" + TestSuite.testDir + "/" + TestSuite.randomString();
        this.repo = new RepoOpener().create(true).open(this.uri, "pwd");
        repo.close();
    }

    @Test(expected = ZboxException.class)
    public void checkRepoExistsNullUri() throws ZboxException {
        Repo.exists(null);
    }

    @Test
    public void checkRepoExists() throws ZboxException {
        assertTrue(Repo.exists(this.uri));
    }

    @Test(expected = ZboxException.class)
    public void openRepoWithNullUri() throws ZboxException {
        new RepoOpener().create(true).open(null, "pwd");
    }

    @Test(expected = ZboxException.class)
    public void wrongPassword01() throws ZboxException {
        new RepoOpener().create(true).open(this.uri, null);
    }

    @Test(expected = ZboxException.class)
    public void wrongPassword02() throws ZboxException {
        new RepoOpener().create(true).open(this.uri, "");
    }

    @Test(expected = ZboxException.class)
    public void wrongPassword03() throws ZboxException {
        new RepoOpener().create(true).open(this.uri, "wrong pwd");
    }

    @Test(expected = ZboxException.class)
    public void openRepoWithCreateNew() throws ZboxException {
        new RepoOpener().createNew(true).open(this.uri, "pwd");
    }

    @Test
    public void openRepo() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(this.uri, "pwd");
        assertNotNull(repo);
        repo.close();
    }

    @Test
    public void closeRepoTwice() throws ZboxException {
        Repo repo = new RepoOpener().create(true).open(this.uri, "pwd");
        repo.close();
        repo.close();
    }

    @After
    public void after() {
    }
}
