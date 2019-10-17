package io.zbox.zboxfs.test.suite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.zbox.zboxfs.Path;
import io.zbox.zboxfs.ZboxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PathTest {
    private static final String TAG = "PathTest";

    @Before
    public void before() {
    }

    @Test(expected = ZboxException.class)
    public void invalidPath() throws ZboxException {
        Path path = new Path("aaa");
    }

    @Test(expected = ZboxException.class)
    public void invalidPath2() throws ZboxException {
        Path path = new Path("");
    }

    @Test(expected = ZboxException.class)
    public void invalidPath3() throws ZboxException {
        Path path = new Path(null);
    }

    @Test
    public void validPath() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa");
        Path path3 = new Path("/aaa/bbb");

        assertEquals(path.toString(), "/");
        assertEquals(path2.toString(), "/aaa");
        assertEquals(path3.toString(), "/aaa/bbb");

        assertTrue(path.isRoot());
        assertFalse(path2.isRoot());
        assertFalse(path3.isRoot());

        assertTrue(path2.equals(new Path(path2.toString())));
        assertFalse(path2.equals(new Path(path3.toString())));
    }

    @Test
    public void parent() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa");
        Path path3 = new Path("/aaa/bbb");
        Path parent = path.parent();
        Path parent2 = path2.parent();
        Path parent3 = path3.parent();

        assertEquals(parent.toString(), "/");
        assertEquals(parent2.toString(), "/");
        assertEquals(parent3.toString(), "/aaa");
    }

    @Test
    public void fileName() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa");
        Path path3 = new Path("/aaa/bbb");

        assertNull(path.fileName());
        assertEquals(path2.fileName(), "aaa");
        assertEquals(path3.fileName(), "bbb");
    }

    @Test
    public void stripPrefix() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");

        assertEquals(path.stripPrefix("/"), "");
        assertEquals(path2.stripPrefix("/aaa"), "bbb/ccc");
        assertEquals(path2.stripPrefix("/aaa/bbb/"), "ccc");
    }

    @After
    public void after() {
    }
}
