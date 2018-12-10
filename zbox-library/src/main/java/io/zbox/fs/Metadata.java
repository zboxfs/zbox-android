package io.zbox.fs;

public class Metadata {
    public FileType fileType;
    public long len;
    public int currVersion;
    public long createdAt;
    public long modifiedAt;

    public boolean isDir() {
        return this.fileType == FileType.DIR;
    }

    public boolean isFile() {
        return this.fileType == FileType.FILE;
    }
}
