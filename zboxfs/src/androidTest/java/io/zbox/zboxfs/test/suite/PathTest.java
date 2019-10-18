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

        assertEquals(new Path().toString(), "/");
        assertEquals(Path.root().toString(), "/");

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
        assertEquals(path2.stripPrefix("/aaa/bbb"), "ccc");
        assertNull(path.stripPrefix(null));
    }

    @Test
    public void startsAndEndsWith() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");

        assertTrue(path.startsWith("/"));
        assertFalse(path.startsWith("/xxx"));
        assertTrue(path.startsWith(""));
        assertTrue(path2.startsWith("/aaa"));
        assertTrue(path2.startsWith("/aaa/bbb"));
        assertTrue(path2.startsWith("/aaa/bbb/ccc"));
        assertFalse(path2.startsWith("/xxx"));
        assertFalse(path2.startsWith("/xxx/yyy"));
        assertTrue(path2.startsWith(""));
        assertFalse(path2.startsWith(null));

        assertTrue(path.endsWith(""));
        assertFalse(path.endsWith("xxx"));
        assertTrue(path2.endsWith("ccc"));
        assertTrue(path2.endsWith("bbb/ccc"));
        assertTrue(path2.endsWith("aaa/bbb/ccc"));
        assertFalse(path2.endsWith("xxx"));
        assertFalse(path2.endsWith("bbb/yyy"));
        assertFalse(path2.endsWith("/aaa"));
        assertFalse(path2.endsWith(null));
    }

    @Test
    public void fileStemAndExtension() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");
        Path path3 = new Path("/aaa/bbb/ddd.txt");
        Path path4 = new Path("/aaa/bbb/eee.fff.ext");

        assertEquals(path.fileStem(), "");
        assertEquals(path2.fileStem(), "ccc");
        assertEquals(path3.fileStem(), "ddd");
        assertEquals(path4.fileStem(), "eee.fff");

        assertEquals(path.extension(), "");
        assertEquals(path2.extension(), "");
        assertEquals(path3.extension(), "txt");
        assertEquals(path4.extension(), "ext");
    }

    @Test
    public void join() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");

        assertTrue(path.join(null).equals("/"));
        assertTrue(path.join("").equals("/"));
        assertTrue(path.join("xxx").equals("/xxx"));
        assertTrue(path.join("xxx/yyy").equals("/xxx/yyy"));
        assertTrue(path.join("/zzz").equals("/zzz"));
        assertTrue(path.join("/zzz/").equals("/zzz/"));
        assertTrue(path.join("//zzz/").equals("//zzz/"));
        assertTrue(path2.join("xxx").equals("/aaa/bbb/ccc/xxx"));
        assertTrue(path2.join("xxx/yyy").equals("/aaa/bbb/ccc/xxx/yyy"));
    }

    @Test
    public void pushAndPop() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");

        path.push(null);
        assertTrue(path.equals("/"));
        path.push("");
        assertTrue(path.equals("/"));
        path.push("xxx");
        assertTrue(path.equals("/xxx"));
        path.push("yyy");
        assertTrue(path.equals("/xxx/yyy"));

        path2.push("xxx");
        assertTrue(path2.equals("/aaa/bbb/ccc/xxx"));

        assertTrue(path.pop());
        assertTrue(path.equals("/xxx"));
        assertTrue(path.pop());
        assertTrue(path.equals("/"));
        assertFalse(path.pop());
        assertTrue(path.equals("/"));

        assertTrue(path2.pop());
        assertTrue(path2.equals("/aaa/bbb/ccc"));
        assertTrue(path2.pop());
        assertTrue(path2.equals("/aaa/bbb"));
        assertTrue(path2.pop());
        assertTrue(path2.equals("/aaa"));
        assertTrue(path2.pop());
        assertTrue(path2.equals("/"));
        assertFalse(path2.pop());
        assertTrue(path2.equals("/"));
    }

    @Test
    public void setFileName() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");
        Path path3 = new Path("/aaa/bbb/ddd.txt");
        Path path4 = new Path("/aaa/bbb/eee.fff.ext");

        path.setFileName("xxx");
        assertTrue(path.equals("/xxx"));
        path.setFileName("yyy.txt");
        assertTrue(path.equals("/yyy.txt"));

        path2.setFileName("xxx");
        assertTrue(path2.equals("/aaa/bbb/xxx"));
        path2.setFileName("yyy.txt");
        assertTrue(path2.equals("/aaa/bbb/yyy.txt"));

        path3.setFileName("xxx");
        assertTrue(path3.equals("/aaa/bbb/xxx"));
        path3.setFileName("yyy.txt");
        assertTrue(path3.equals("/aaa/bbb/yyy.txt"));

        path4.setFileName("xxx");
        assertTrue(path4.equals("/aaa/bbb/xxx"));
        path4.setFileName("yyy.txt");
        assertTrue(path4.equals("/aaa/bbb/yyy.txt"));
    }

    @Test
    public void setExtension() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");
        Path path3 = new Path("/aaa/bbb/ddd.txt");
        Path path4 = new Path("/aaa/bbb/eee.fff.ext");

        path.setExtension("ext");
        assertTrue(path.equals("/"));

        path2.setExtension("ext");
        assertTrue(path2.equals("/aaa/bbb/ccc.ext"));

        path3.setExtension("ext");
        assertTrue(path3.equals("/aaa/bbb/ddd.ext"));

        path4.setExtension("ext");
        assertTrue(path4.equals("/aaa/bbb/eee.fff.ext"));
        path4.setExtension("ext2");
        assertTrue(path4.equals("/aaa/bbb/eee.fff.ext2"));
    }

    @Test
    public void components() throws ZboxException {
        Path path = new Path("/");
        Path path2 = new Path("/aaa/bbb/ccc");
        Path path3 = new Path("/aaa/bbb/ddd.txt");
        Path path4 = new Path("/aaa/bbb/eee.fff.ext");
        String[] comps;

        comps = path.components();
        assertEquals(comps.length, 1);
        assertEquals(comps[0], "/");

        comps = path2.components();
        assertEquals(comps.length, 4);
        assertEquals(comps[0], "/");
        assertEquals(comps[1], "aaa");
        assertEquals(comps[2], "bbb");
        assertEquals(comps[3], "ccc");

        comps = path3.components();
        assertEquals(comps.length, 4);
        assertEquals(comps[0], "/");
        assertEquals(comps[1], "aaa");
        assertEquals(comps[2], "bbb");
        assertEquals(comps[3], "ddd.txt");

        comps = path4.components();
        assertEquals(comps.length, 4);
        assertEquals(comps[0], "/");
        assertEquals(comps[1], "aaa");
        assertEquals(comps[2], "bbb");
        assertEquals(comps[3], "eee.fff.ext");
    }

    @After
    public void after() {
    }
}
