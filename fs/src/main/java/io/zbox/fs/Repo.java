package io.zbox.fs;

import androidx.annotation.NonNull;

/**
 * <p>The {@code Repo} class represents an encrypted repository containing the whole file system.</p>
 *
 * <p>A {@code Repo} represents a secure collection which consists of files, directories and their
 * associated data. {@code Repo} provides methods to manipulate the enclosed file system.</p>
 *
 * <h3>Storages</h3>
 *
 * <p>ZboxFS supports a variety of underlying storages, which are listed below.</p>
 *
 * <table border="1">
 *     <tr><th>Storage</th><th>URI identifier</th></tr>
 *     <tr><td>Memory</td><td><i>"mem://"</i></td></tr>
 *     <tr><td>OS file system</td><td><i>"file://"</i></td></tr>
 *     <tr><td><a href="https://zbox.io">Zbox Cloud Storage</a></td><td><i>"zbox://"</i></td></tr>
 * </table>
 *
 * <p>* Visit <a href="https://zbox.io">zbox.io</a> to learn more about Zbox Cloud Storage.</p>
 *
 * <h3>Create and open repo</h3>
 *
 * <p>{@code Repo} can be created on different underlying storage using {@link io.zbox.fs.RepoOpener}.
 * It uses an URI-like string to specify its storage type and location. The URI string starts with an
 * identifier which specifies the storage type, as shown in above table. You can check more location
 * URI details at {@link io.zbox.fs.RepoOpener}.</p>
 *
 * <p>{@code Repo} can only be opened once at a time. After opened, it keeps locked from other open
 * attempts until it is closed.
 *
 * <p>Optionally, {@code Repo} can be opened in read-only mode if you only need read access.</p>
 *
 * @author Bo Lu
 * @see io.zbox.fs.File
 *
 */
public class Repo extends RustObject {

    private static int rustObjId = 101;

    private Repo() {
    }

    public static boolean exists(String uri) throws ZboxException {
        checkNullParam(uri);
        return jniExists(uri);
    }

    public RepoInfo info() {
        return this.jniInfo();
    }

    public void resetPassword(String oldPwd, String newPwd,
                              OpsLimit opsLimit, MemLimit memLimit) throws ZboxException {
        checkNullParam2(oldPwd, newPwd);
        checkNullParam2(opsLimit, memLimit);
        this.jniResetPassword(oldPwd, newPwd, opsLimit.getValue(), memLimit.getValue());
    }

    public static void repairSuperBlock(String uri, String pwd) throws ZboxException {
        checkNullParam2(uri, pwd);
        jniRepairSuperBlock(uri, pwd);
    }

    public boolean pathExists(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniPathExists(path);
    }

    public boolean isFile(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniIsFile(path);
    }

    public boolean isDir(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniIsDir(path);
    }

    public File createFile(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniCreateFile(path);
    }

    public File openFile(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniOpenFile(path);
    }

    public void createDir(String path) throws ZboxException {
        checkNullParam(path);
        this.jniCreateDir(path);
    }

    public void createDirAll(String path) throws ZboxException {
        checkNullParam(path);
        this.jniCreateDirAll(path);
    }

    public DirEntry[] readDir(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniReadDir(path);
    }

    public Metadata metadata(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniMetadata(path);
    }

    public Version[] history(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniHistory(path);
    }

    public void copy(String from, String to) throws ZboxException {
        checkNullParam2(from, to);
        this.jniCopy(from, to);
    }

    public void removeFile(String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveFile(path);
    }

    public void removeDir(String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveDir(path);
    }

    public void removeDirAll(String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveDirAll(path);
    }

    public void rename(String from, String to) throws ZboxException {
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
