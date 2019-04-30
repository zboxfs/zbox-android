package io.zbox.fs;

public class Repo extends RustObject {

    private static int rustObjId = 101;

    private Repo() {}

    public static boolean exists(String uri) {
        return jniExists(uri);
    }

    public RepoInfo info() {
        return this.jniInfo();
    }

    public void resetPassword(String oldPwd, String newPwd, OpsLimit opsLimit, MemLimit memLimit) {
        this.jniResetPassword(oldPwd, newPwd, opsLimit.getValue(), memLimit.getValue());
    }

    public void repairSuperBlock(String uri, String pwd) {
        this.jniRepairSuperBlock(uri, pwd);
    }

    public boolean pathExists(String path) {
        return this.jniPathExists(path);
    }

    public boolean isFile(String path) {
        return this.jniIsFile(path);
    }

    public boolean isDir(String path) {
        return this.jniIsDir(path);
    }

    public File createFile(String path) {
        return this.jniCreateFile(path);
    }

    public File openFile(String path) {
        return this.jniOpenFile(path);
    }

    public void createDir(String path) {
        this.jniCreateDir(path);
    }

    public void createDirAll(String path) {
        this.jniCreateDirAll(path);
    }

    public DirEntry[] readDir(String path) {
        return this.jniReadDir(path);
    }

    public Metadata metadata(String path) {
        return this.jniMetadata(path);
    }

    public Version[] history(String path) {
        return this.jniHistory(path);
    }

    public void copy(String from, String to) {
        this.jniCopy(from, to);
    }

    public void removeFile(String path) {
        this.jniRemoveFile(path);
    }

    public void removeDir(String path) {
        this.jniRemoveDir(path);
    }

    public void removeDirAll(String path) {
        this.jniRemoveDirAll(path);
    }

    public void rename(String from, String to) {
        this.jniRename(from, to);
    }

    // jni methods
    private native static boolean jniExists(String uri);
    private native RepoInfo jniInfo();
    private native void jniResetPassword(String oldPwd, String newPwd, int opsLimit, int memLimit);
    private native void jniRepairSuperBlock(String uri, String pwd);
    private native boolean jniPathExists(String path);
    private native boolean jniIsFile(String path);
    private native boolean jniIsDir(String path);
    private native File jniCreateFile(String path);
    private native File jniOpenFile(String path);
    private native void jniCreateDir(String path);
    private native void jniCreateDirAll(String path);
    private native DirEntry[] jniReadDir(String path);
    private native Metadata jniMetadata(String path);
    private native Version[] jniHistory(String path);
    private native void jniCopy(String from, String to);
    private native void jniRemoveFile(String path);
    private native void jniRemoveDir(String path);
    private native void jniRemoveDirAll(String path);
    private native void jniRename(String from, String to);
}
