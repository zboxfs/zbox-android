package io.zbox.fs;

public class RepoOpener extends RustObject {

    private static int rustObjId = 100;

    public RepoOpener() { }

    public void opsLimit(OpsLimit limit) {
        this.jniOpsLimit(limit.getValue());
    }

    public void memLimit(MemLimit limit) {
        this.jniMemLimit(limit.getValue());
    }

    public void cipher(Cipher cipher) {
        this.jniCipher(cipher.getValue());
    }

    public void create(boolean create) {
        this.jniCreate(create);
    }

    public void createNew(boolean createNew) {
        this.jniCreateNew(createNew);
    }

    public void compress(boolean compress) {
        this.jniCompress(compress);
    }

    public void versionLimit(int limit) {
        this.jniVersionLimit(limit);
    }

    public void dedupChunk(boolean dedup) {
        this.jniDedupChunk(dedup);
    }

    public void readOnly(boolean readOnly) {
        this.jniReadOnly(readOnly);
    }

    /**
     * Allocates a new direct byte buffer.
     *
     * <p> The new buffer's position will be zero, its limit will be its
     * capacity, its mark will be undefined, and each of its elements will be
     * initialized to zero.  Whether or not it has a
     * is unspecified.</p>
     *
     * @param  uri
     *         The new buffer's capacity, in bytes
     *
     * @param  pwd
     *         The password to encrypt repo
     *
     * @return  The opened repo instance
     *
     * @throws  ZboxException
     */
    public Repo open(String uri, String pwd) throws ZboxException {
        Repo repo = this.jniOpen(uri, pwd);
        return repo;
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
    private native Repo jniOpen(String uri, String pwd);
}

