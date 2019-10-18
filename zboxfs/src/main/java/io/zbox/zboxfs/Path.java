package io.zbox.zboxfs;

public class Path {

    private String path;

    public Path() {
        this.path = "/";
    }

    public Path(String path) throws ZboxException {
        jniValidate(path);
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public static Path root() {
        return new Path();
    }

    public boolean isRoot() {
        return path.equals("/");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }

        return path.equals(((Path)other).path);
    }

    public boolean equals(Path other) {
        if (other == null) return false;
        return path.equals(other.path);
    }

    public boolean equals(String other) {
        if (other == null) return false;
        return path.equals(other);
    }

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
        if (base == null) return null;
        return jniStripPrefix(path, base);
    }

    public boolean startsWith(String base) {
        if (base == null) return false;
        return jniStartsWith(path, base);
    }

    public boolean endsWith(String child) {
        if (child == null) return false;
        return jniEndsWith(path, child);
    }

    public String fileStem() {
        return jniFileStem(path);
    }

    public String extension() {
        return jniExtension(path);
    }

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

    public void push(String path) {
        if (path == null) return;
        this.path = jniPush(this.path, path);
    }

    public boolean pop() {
        String newPath = jniPop(this.path);
        boolean ret = !this.path.equals(newPath);
        this.path = newPath;
        return ret;
    }

    public void setFileName(String fileName) {
        if (fileName == null) return;
        this.path = jniSetFileName(this.path, fileName);
    }

    public void setExtension(String extension) {
        if (extension == null) return;
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

    private native String jniPush(String path, String other);

    private native String jniPop(String path);

    private native String jniSetFileName(String path, String fileName);

    private native String jniSetExtension(String path, String ext);

    private native String[] jniComponents(String path);

}