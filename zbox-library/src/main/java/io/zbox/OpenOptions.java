package io.zbox;

public class OpenOptions extends RustObject {

    private static int rustObjId = 102;

    private OpenOptions() {}

    public void read(boolean read) {
        this.jniRead(read);
    }

    public void write(boolean write) {
        this.jniWrite(write);
    }

    public void append(boolean append) {
        this.jniAppend(append);
    }

    public void truncate(boolean truncate) {
        this.jniTruncate(truncate);
    }

    public void create(boolean create) {
        this.jniCreate(create);
    }

    public void createNew(boolean createNew) {
        this.jniCreateNew(createNew);
    }

    public void versionLimit(int limit) {
        this.jniVersionLimit(limit);
    }

    public void dedupChunk(boolean dedup) {
        this.jniDedupChunk(dedup);
    }

    public File open(Repo repo, String path) throws ZboxException {
        File file = this.jniOpen(repo, path);
        return file;
    }

    // jni methods
    private native void jniRead(boolean read);
    private native void jniWrite(boolean write);
    private native void jniAppend(boolean append);
    private native void jniTruncate(boolean truncate);
    private native void jniCreate(boolean create);
    private native void jniCreateNew(boolean createNew);
    private native void jniVersionLimit(int limit);
    private native void jniDedupChunk(boolean dedup);
    private native File jniOpen(Repo repo, String path);
}
