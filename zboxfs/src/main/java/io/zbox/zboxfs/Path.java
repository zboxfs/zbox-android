package io.zbox.zboxfs;

/**
 * This class represents an absolute location of file or directory in ZboxFS repository.
 *
 * <p>{@code Path} is always absolute and the separator character is <b>"/"</b>.</p>
 *
 * @author Bo Lu
 */
public class Path implements Cloneable {

    private String path;

    /**
     * Create a path points to the root directory "/".
     */
    public Path() {
        this.path = "/";
    }

    /**
     * Create a path from string.
     *
     * <blockquote><pre>
     * Path path = new Path("/foo/bar");
     * </pre></blockquote>
     *
     * @param path the absolute path in string, e.g. "/foo/bar"
     * @throws ZboxException if {@code path} is null or not absolute
     */
    public Path(String path) throws ZboxException {
        jniValidate(path);
        this.path = path;
    }

    /**
     * Convert a path to string.
     *
     * @return path in string
     */
    @Override
    public String toString() {
        return path;
    }

    /**
     * Create a path points to the root directory "/".
     *
     * @return a path points to the root directory
     */
    public static Path root() {
        return new Path();
    }

    /**
     * Check if it is root directory "/".
     *
     * @return {@code true} if it is root directory, {@code false} otherwise.
     */
    public boolean isRoot() {
        return path.equals("/");
    }

    /**
     * Compare this path to another path.
     *
     * @param other the other path to compare with
     * @return {@code true} if both paths points same location, {@code false} if {@code other} is
     * not a {@code Path} instance or points different location
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }

        return path.equals(((Path) other).path);
    }

    /**
     * Compare this path to another path.
     *
     * @param other the other path to compare with
     * @return {@code true} if both paths point to same location, {@code false} otherwise
     */
    public boolean equals(Path other) {
        if (other == null) return false;
        return path.equals(other.path);
    }

    /**
     * Compare this path to another path.
     *
     * @param other the other path to compare with, in string
     * @return {@code true} if both paths point to same location, {@code false} otherwise
     */
    public boolean equals(String other) {
        if (other == null) return false;
        return path.equals(other);
    }

    /**
     * Returns the path without its final component.
     *
     * <p>Return root path again if this path is a root.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb");
     *
     * assertEquals(path.parent().toString(), "/");
     * assertEquals(path2.parent().toString(), "/aaa");
     * </pre></blockquote>
     *
     * @return path of parent directory
     */
    public Path parent() {
        String parent = jniParent(path);
        Path ret = null;
        try {
            ret = new Path(parent);
        } catch (ZboxException ignore) {
            // never reach here
        }
        return ret;
    }

    /**
     * Returns the final component of the path.
     *
     * <p>If the path is a normal file, this is the file name. If it's the path of a directory, this
     * is the directory name.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb");
     * Path path3 = new Path("/aaa/ccc.txt");
     *
     * assertEquals(path.fileName(), "");
     * assertEquals(path2.fileName(), "bbb");
     * assertEquals(path3.fileName(), "ccc.txt");
     * </pre></blockquote>
     *
     * @return final component of the path
     */
    public String fileName() {
        return jniFileName(path);
    }

    /**
     * Returns a string that with {@code base} removed.
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb/ccc");
     *
     * assertEquals(path.stripPrefix("/"), "");
     * assertEquals(path2.stripPrefix("/aaa"), "bbb/ccc");
     * assertEquals(path2.stripPrefix("/aaa/bbb/"), "ccc");
     * </pre></blockquote>
     *
     * @param base the path to be removed
     * @return a string with {@code base} removed.
     */
    public String stripPrefix(String base) {
        if (base == null) return null;
        return jniStripPrefix(path, base);
    }

    /**
     * Determines whether {@code base} is a prefix of this path.
     *
     * <p>Only considers whole path components to match.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb/ccc");
     *
     * assertTrue(path.startsWith("/"));
     * assertTrue(path2.startsWith("/aaa"));
     * assertTrue(path2.startsWith("/aaa/bbb"));
     * </pre></blockquote>
     *
     * @param base the prefix to compare with
     * @return true if this path has prefix {@code base}, false otherwise
     */
    public boolean startsWith(String base) {
        if (base == null) return false;
        return jniStartsWith(path, base);
    }

    /**
     * Determines whether {@code child} is a suffix of this path.
     *
     * <p>Only considers whole path components to match.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb/ccc");
     *
     * assertTrue(path.endsWith(""));
     * assertTrue(path2.endsWith("ccc"));
     * assertTrue(path2.endsWith("bbb/ccc"));
     * </pre></blockquote>
     *
     * @param child the suffix to compare with
     * @return true if this path has suffix {@code child}, false otherwise
     */
    public boolean endsWith(String child) {
        if (child == null) return false;
        return jniEndsWith(path, child);
    }

    /**
     * Extracts the stem (non-extension) portion from file name of this path.
     *
     * <blockquote><pre>
     * Path path = new Path("/");
     * Path path2 = new Path("/aaa/bbb/ccc");
     * Path path3 = new Path("/aaa/bbb/ddd.txt");
     *
     * assertEquals(path.fileStem(), "");
     * assertEquals(path2.fileStem(), "ccc");
     * assertEquals(path3.fileStem(), "ddd");
     * </pre></blockquote>
     *
     * @return file stem
     */
    public String fileStem() {
        return jniFileStem(path);
    }

    /***
     * Extracts the extension of from file name of this path, if possible.
     *
     * <blockquote><pre>
     * Path path = new Path("/");
     * Path path2 = new Path("/aaa/bbb/ccc");
     * Path path3 = new Path("/aaa/bbb/ddd.txt");
     *
     * assertEquals(path.extension(), "");
     * assertEquals(path2.extension(), "");
     * assertEquals(path3.extension(), "txt");
     * </pre></blockquote>
     *
     * @return file extension
     */
    public String extension() {
        return jniExtension(path);
    }

    /**
     * Creates a new {@code Path} instance with path adjoined to this path.
     *
     * <blockquote><pre>
     * Path path = new Path("/");
     * Path path2 = new Path("/aaa/bbb/ccc");
     *
     * assertTrue(path.join("xxx").equals("/xxx"));
     * assertTrue(path.join("xxx/yyy").equals("/xxx/yyy"));
     * assertTrue(path2.join("xxx").equals("/aaa/bbb/ccc/xxx"));
     * assertTrue(path2.join("xxx/yyy").equals("/aaa/bbb/ccc/xxx/yyy"));
     * </pre></blockquote>
     *
     * @param path the path to join to
     * @return a new path which is joined together
     */
    public Path join(String path) {
        if (path == null) return this;
        String newPath = jniJoin(this.path, path);
        Path ret = null;
        try {
            ret = new Path(newPath);
        } catch (ZboxException ignore) {
            // never reach here
        }
        return ret;
    }

    /**
     * Extends the path itself with {#code path}.
     *
     * <p>If {#code path} is absolute, it replaces the current path.</p>
     *
     * <p>This will modify the path in place.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     *
     * path.push("xxx");
     * assertTrue(path.equals("/xxx"));
     * path.push("yyy");
     * assertTrue(path.equals("/xxx/yyy"));
     * path.push("/zzz");
     * assertTrue(path.equals("/zzz"));
     * </pre></blockquote>
     *
     * @param path the path to be extended with
     */
    public void push(String path) {
        if (path == null) return;
        this.path = jniPush(this.path, path);
    }

    /**
     * Truncates the path itself to its parent.
     *
     * <p>This will modify the path in place.</p>
     *
     * <blockquote><pre>
     * Path path = new Path("/aaa/bbb/ccc");
     *
     * assertTrue(path.pop());
     * assertTrue(path.equals("/aaa/bbb"));
     * assertTrue(path.pop());
     * assertTrue(path.equals("/aaa"));
     * assertTrue(path.pop());
     * assertTrue(path.equals("/"));
     * assertFalse(path.pop());
     * assertTrue(path.equals("/"));
     * </pre></blockquote>
     *
     * @return true if any path components are truncated, false otherwise
     */
    public boolean pop() {
        String newPath = jniPop(this.path);
        boolean ret = !this.path.equals(newPath);
        this.path = newPath;
        return ret;
    }

    /**
     * Updates this file name of this path to {@code fileName}.
     *
     * <p>This will modify the path in place.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb/ccc");
     *
     * path.setFileName("xxx");
     * assertTrue(path.equals("/xxx"));
     * path.setFileName("yyy.txt");
     * assertTrue(path.equals("/yyy.txt"));
     *
     * path2.setFileName("xxx");
     * assertTrue(path2.equals("/aaa/bbb/xxx"));
     * path2.setFileName("yyy.txt");
     * assertTrue(path2.equals("/aaa/bbb/yyy.txt"));
     * </pre></blockquote>
     *
     * @param fileName the file name to be updated with
     */
    public void setFileName(String fileName) {
        if (fileName == null) return;
        this.path = jniSetFileName(this.path, fileName);
    }

    /**
     * Updates the extension of this path to {@code extension}.
     *
     * <p>If the file name of this path has no extension, the extension is added; otherwise it is
     * replaced.</p>
     *
     * <p>This will modify the path in place.</p>
     *
     * <blockquote><pre>
     * Path path = Path.root();
     * Path path2 = new Path("/aaa/bbb/ccc");
     * Path path3 = new Path("/aaa/bbb/ddd.txt");
     *
     * path.setExtension("ext");
     * assertTrue(path.equals("/"));
     *
     * path2.setExtension("ext");
     * assertTrue(path2.equals("/aaa/bbb/ccc.ext"));
     *
     * path3.setExtension("ext");
     * assertTrue(path3.equals("/aaa/bbb/ddd.ext"));
     * </pre></blockquote>
     *
     * @param extension the extension to be updated with
     */
    public void setExtension(String extension) {
        if (extension == null) return;
        this.path = jniSetExtension(this.path, extension);
    }

    /**
     * Produces an array of all components of this path.
     *
     * <blockquote><pre>
     * Path path = new Path("/");
     * Path path2 = new Path("/aaa/bbb/ccc");
     * String[] comps;
     *
     * comps = path.components();
     * assertEquals(comps.length, 1);
     * assertEquals(comps[0], "/");
     *
     * comps = path2.components();
     * assertEquals(comps.length, 4);
     * assertEquals(comps[0], "/");
     * assertEquals(comps[1], "aaa");
     * assertEquals(comps[2], "bbb");
     * assertEquals(comps[3], "ccc");
     * </pre></blockquote>
     *
     * @return an array of all components of this path
     */
    public String[] components() {
        return jniComponents(this.path);
    }

    @Override
    protected Path clone() throws CloneNotSupportedException {
        super.clone();
        Path ret = new Path();
        ret.path = this.path;
        return ret;
    }

    // jni methods
    private native void jniValidate(String path) throws ZboxException;

    private native String jniParent(String path);

    private native String jniFileName(String path);

    private native String jniStripPrefix(String path, String base);

    private native boolean jniStartsWith(String path, String base);

    private native boolean jniEndsWith(String path, String child);

    private native String jniFileStem(String path);

    private native String jniExtension(String path);

    private native String jniJoin(String path, String path2);

    private native String jniPush(String path, String other);

    private native String jniPop(String path);

    private native String jniSetFileName(String path, String fileName);

    private native String jniSetExtension(String path, String ext);

    private native String[] jniComponents(String path);

}