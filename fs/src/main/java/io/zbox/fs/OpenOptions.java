package io.zbox.fs;

import androidx.annotation.NonNull;

public class OpenOptions extends RustObject {

    private static int rustObjId = 102;

    public OpenOptions() {
    }

    public OpenOptions read(boolean read) {
        this.jniRead(read);
        return this;
    }

    public OpenOptions write(boolean write) {
        this.jniWrite(write);
        return this;
    }

    public OpenOptions append(boolean append) {
        this.jniAppend(append);
        return this;
    }

    public OpenOptions truncate(boolean truncate) {
        this.jniTruncate(truncate);
        return this;
    }

    public OpenOptions create(boolean create) {
        this.jniCreate(create);
        return this;
    }

    public OpenOptions createNew(boolean createNew) {
        this.jniCreateNew(createNew);
        return this;
    }

    public OpenOptions versionLimit(int limit) {
        this.jniVersionLimit(limit);
        return this;
    }

    public OpenOptions dedupChunk(boolean dedup) {
        this.jniDedupChunk(dedup);
        return this;
    }

    public File open(@NonNull Repo repo, @NonNull String path) throws ZboxException {
        checkNullParam2(repo, path);
        return this.jniOpen(repo, path);
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
