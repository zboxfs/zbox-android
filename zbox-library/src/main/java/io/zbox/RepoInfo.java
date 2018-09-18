package io.zbox;

public class RepoInfo {
    public byte[] volumeId;
    public String version;
    public String uri;
    public OpsLimit opsLimit;
    public MemLimit memLimit;
    public Cipher cipher;
    public boolean compress;
    public int versionLimit;
    public boolean dedupChunk;
    public boolean isReadOnly;
    public long createdAt;
}

