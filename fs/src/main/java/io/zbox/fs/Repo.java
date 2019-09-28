package io.zbox.fs;

import androidx.annotation.NonNull;

public class Repo extends RustObject {

    private static int rustObjId = 101;

    private Repo() {
    }

    public static boolean exists(@NonNull String uri) throws ZboxException {
        checkNullParam(uri);
        return jniExists(uri);
    }

    public RepoInfo info() {
        return this.jniInfo();
    }

    public void resetPassword(@NonNull String oldPwd, @NonNull String newPwd,
                              @NonNull OpsLimit opsLimit, @NonNull MemLimit memLimit) throws ZboxException {
        checkNullParam2(oldPwd, newPwd);
        checkNullParam2(opsLimit, memLimit);
        this.jniResetPassword(oldPwd, newPwd, opsLimit.getValue(), memLimit.getValue());
    }

    public static void repairSuperBlock(@NonNull String uri, @NonNull String pwd) throws ZboxException {
        checkNullParam2(uri, pwd);
        jniRepairSuperBlock(uri, pwd);
    }

    public boolean pathExists(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniPathExists(path);
    }

    public boolean isFile(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniIsFile(path);
    }

    public boolean isDir(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniIsDir(path);
    }

    public File createFile(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniCreateFile(path);
    }

    public File openFile(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniOpenFile(path);
    }

    public void createDir(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        this.jniCreateDir(path);
    }

    public void createDirAll(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        this.jniCreateDirAll(path);
    }

    public DirEntry[] readDir(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniReadDir(path);
    }

    public Metadata metadata(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniMetadata(path);
    }

    public Version[] history(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        return this.jniHistory(path);
    }

    public void copy(@NonNull String from, @NonNull String to) throws ZboxException {
        checkNullParam2(from, to);
        this.jniCopy(from, to);
    }

    public void removeFile(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveFile(path);
    }

    public void removeDir(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveDir(path);
    }

    public void removeDirAll(@NonNull String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveDirAll(path);
    }

    public void rename(@NonNull String from, @NonNull String to) throws ZboxException {
        checkNullParam2(from, to);
        this.jniRename(from, to);
    }

    // jni methods
    private native static boolean jniExists(String uri);

    private native RepoInfo jniInfo();

    private native void jniResetPassword(String oldPwd, String newPwd, int opsLimit, int memLimit);

    private static native void jniRepairSuperBlock(String uri, String pwd);

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
