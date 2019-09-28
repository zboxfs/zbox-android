package io.zbox.fs;

import androidx.annotation.NonNull;

public class RepoOpener extends RustObject {

    private static int rustObjId = 100;

    public RepoOpener() {
    }

    public RepoOpener opsLimit(@NonNull OpsLimit limit) {
        OpsLimit lim = limit == null ? OpsLimit.INTERACTIVE : limit;
        this.jniOpsLimit(lim.getValue());
        return this;
    }

    public RepoOpener memLimit(@NonNull MemLimit limit) {
        MemLimit lim = limit == null ? MemLimit.INTERACTIVE : limit;
        this.jniMemLimit(lim.getValue());
        return this;
    }

    public RepoOpener cipher(@NonNull Cipher cipher) {
        Cipher ci = cipher == null ? Cipher.XCHACHA : cipher;
        this.jniCipher(ci.getValue());
        return this;
    }

    public RepoOpener create(boolean create) {
        this.jniCreate(create);
        return this;
    }

    public RepoOpener createNew(boolean createNew) {
        this.jniCreateNew(createNew);
        return this;
    }

    public RepoOpener compress(boolean compress) {
        this.jniCompress(compress);
        return this;
    }

    public RepoOpener versionLimit(int limit) {
        this.jniVersionLimit(limit);
        return this;
    }

    public RepoOpener dedupChunk(boolean dedup) {
        this.jniDedupChunk(dedup);
        return this;
    }

    public RepoOpener readOnly(boolean readOnly) {
        this.jniReadOnly(readOnly);
        return this;
    }

    public RepoOpener force(boolean force) {
        this.jniForce(force);
        return this;
    }

    /**
     * Allocates a new direct byte buffer.
     *
     * <p> The new buffer's position will be zero, its limit will be its
     * capacity, its mark will be undefined, and each of its elements will be
     * initialized to zero.  Whether or not it has a
     * is unspecified.</p>
     *
     * @param uri The new buffer's capacity, in bytes
     * @param pwd The password to encrypt repo
     * @return The opened repo instance
     * @throws ZboxException
     */
    public Repo open(@NonNull String uri, @NonNull String pwd) throws ZboxException {
        checkNullParam2(uri, pwd);
        return this.jniOpen(uri, pwd);
    }

    // jni methods
    private native void jniOpsLimit(int limit);

    private native void jniMemLimit(int limit);

    private native void jniCipher(int cipher);

    private native void jniCreate(boolean create);

    private native void jniCreateNew(boolean createNew);

    private native void jniCompress(boolean compress);

    private native void jniVersionLimit(int limit);

    private native void jniDedupChunk(boolean dedup);

    private native void jniReadOnly(boolean readOnly);

    private native void jniForce(boolean force);

    private native Repo jniOpen(String uri, String pwd);
}

