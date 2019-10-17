package io.zbox.zboxfs;

public class Path {

    private String path;

    public Path(String path) throws ZboxException {
        jniValidate(path);
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public boolean isRoot() { return path.equals("/"); }

    public boolean equals(Path other) { return path.equals(other.path); }

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

    public String fileName() {
        return jniFileName(path);
    }

    public String stripPrefix(String base) {
        return jniStripPrefix(path, base);
    }

    public boolean startsWith(String base) {
        return jniStartsWith(path, base);
    }

    public boolean endsWith(String child) {
        return jniEndsWith(path, child);
    }

    public String fileStem() {
        return jniFileStem(path);
    }

    public String extension() {
        return jniExtension(path);
    }

    public void join(String path) {
        this.path = jniJoin(this.path, path);
    }

    public void setFileName(String fileName) {
        this.path = jniSetFileName(this.path, fileName);
    }

    public void setExtension(String extension) {
        this.path = jniSetExtension(this.path, extension);
    }

    public String[] components() {
        return jniComponents(this.path);
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

    private native String jniSetFileName(String path, String fileName);

    private native String jniSetExtension(String path, String ext);

    private native String[] jniComponents(String path);

}