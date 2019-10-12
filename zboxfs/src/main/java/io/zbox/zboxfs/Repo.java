package io.zbox.zboxfs;

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
 * <table border="1" summary="">
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
 * <p>{@code Repo} can be created on different underlying storage using {@link RepoOpener}.
 * It uses an URI-like string to specify its storage type and location. The URI string starts with an
 * identifier which specifies the storage type, as shown in above table. You can check more location
 * URI details at {@link RepoOpener}.</p>
 *
 * <p>{@code Repo} can only be opened once at a time. After opened, it keeps locked from other open
 * attempts until it is closed.
 *
 * <p>Optionally, {@code Repo} can be opened in read-only mode if you only need read access.</p>
 *
 * <h3>Examples</h3>
 *
 * <p>Create an OS file system based repository.</p>
 *
 * <blockquote><pre>
 * // initialise ZboxFS environment, called first and once
 * Env.init(Env.LOG_DEBUG);
 *
 * // create a OS file system based repository
 * try {
 *     Repo repo = new RepoOpener().create(true).open("file:///path/to/repo", "pwd");
 *     repo.close();
 * } catch (ZboxException err) {
 *     System.out.println("Error: " + err);
 * }
 * </pre></blockquote>
 *
 * <p>Create a memory based repository.</p>
 *
 * <blockquote><pre>
 * Repo repo = new RepoOpener().create(true).open("mem://foo", "pwd");
 * </pre></blockquote>
 *
 * <p>Create a Zbox Cloud Storage repository.</p>
 *
 * <blockquote><pre>
 * Repo repo = new RepoOpener().create(true).open("zbox://9ArJY6wMBULS7V3kXKxqv3XP@jJT5sSjAf3W5cW", "pwd");
 * </pre></blockquote>
 *
 * <p>Open a repository in read-only mode.</p>
 *
 * <blockquote><pre>
 * Repo repo = new RepoOpener()
 *                  .create(true)
 *                  .readOnly(true)
 *                  .open("mem://foo", "pwd");
 * </pre></blockquote>
 *
 * @author Bo Lu
 * @see File
 *
 */
public class Repo extends RustObject {

    private static final int rustObjId = 101;

    private Repo() {
    }

    /**
     * Returns whether the URI points at an existing repository.
     *
     * @param uri the repository URI
     * @return {@code true} if repository existing, otherwise {@code false}
     * @throws ZboxException if any error happened
     */
    public static boolean exists(String uri) throws ZboxException {
        checkNullParam(uri);
        return jniExists(uri);
    }

    /**
     * Get repository metadata information.
     *
     * @return repository metadata information
     */
    public RepoInfo info() {
        return this.jniInfo();
    }

    /**
     * Reset password for the repository.
     *
     * <p>Note: if this method failed due to IO error, super block might be damaged. If it is the
     * case, use {@link #repairSuperBlock(String, String)} to restore super block before re-opening
     * the repo.</p>
     *
     * @param oldPwd old password
     * @param newPwd new password
     * @param opsLimit password encryption operation limit
     * @param memLimit password encryption memory limit
     * @throws ZboxException if any error happened
     * @see #repairSuperBlock(String, String)
     */
    public void resetPassword(String oldPwd, String newPwd,
                              OpsLimit opsLimit, MemLimit memLimit) throws ZboxException {
        checkNullParam2(oldPwd, newPwd);
        checkNullParam2(opsLimit, memLimit);
        this.jniResetPassword(oldPwd, newPwd, opsLimit.getValue(), memLimit.getValue());
    }

    /**
     * Repair possibly damaged super block.
     *
     * <p>This method will try to repair super block using backup. One scenario is when
     * {@link #resetPassword(String, String, OpsLimit, MemLimit)} failed due to IO error, super
     * block might be damaged. Using this method can restore the damaged super block from backup. If
     * super block is all good, this method is no-op.</p>
     *
     * <p>This method is not useful for memory-based storage and must be called when repo is closed.
     * </p>
     *
     * @param uri repository URI
     * @param pwd password
     * @throws ZboxException if any error happened
     * @see #resetPassword(String, String, OpsLimit, MemLimit)
     */
    public static void repairSuperBlock(String uri, String pwd) throws ZboxException {
        checkNullParam2(uri, pwd);
        jniRepairSuperBlock(uri, pwd);
    }

    /**
     * Returns whether the path points at an existing entity in repository.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the file or directory
     * @return {@code true} if the path exists, otherwise {@code false}
     * @throws ZboxException if any error happened
     */
    public boolean pathExists(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniPathExists(path);
    }

    /**
     * Returns whether the path exists in repository and is pointing at a regular file.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the file
     * @return {@code true} if the file exists, otherwise {@code false}
     * @throws ZboxException if any error happened
     */
    public boolean isFile(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniIsFile(path);
    }

    /**
     * Returns whether the path exists in repository and is pointing at a directory.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the directory
     * @return {@code true} if the directory exists, otherwise {@code false}
     * @throws ZboxException if any error happened
     */
    public boolean isDir(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniIsDir(path);
    }

    /**
     * Create a file in read-write mode.
     *
     * <p>This method will create a file if it does not exist, and will truncate it if it does.</p>
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the file to be created
     * @return {@code File} instance created
     * @throws ZboxException if any error happened
     * @see OpenOptions#open(Repo, String)
     */
    public File createFile(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniCreateFile(path);
    }

    /**
     * Attempts to open a file in read-only mode.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the file to be opened
     * @return {@code File} instance opened
     * @throws ZboxException if any error happened
     * @see OpenOptions#open(Repo, String)
     */
    public File openFile(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniOpenFile(path);
    }

    /**
     * Creates a new, empty directory at the specified path.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the directory to be created
     * @throws ZboxException if any error happened
     * @see #createDirAll(String)
     */
    public void createDir(String path) throws ZboxException {
        checkNullParam(path);
        this.jniCreateDir(path);
    }

    /**
     * Recursively create a directory and all of its parent components if they are missing.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the directory to be created
     * @throws ZboxException if any error happened
     * @see #createDir(String)
     */
    public void createDirAll(String path) throws ZboxException {
        checkNullParam(path);
        this.jniCreateDirAll(path);
    }

    /**
     * Returns a vector of all the entries within a directory.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the directory to be read
     * @return array of directory entries
     * @throws ZboxException if any error happened
     */
    public DirEntry[] readDir(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniReadDir(path);
    }

    /**
     * Get the metadata about a file or directory at specified path.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the file or directory
     * @return {@code File} or {@code Directory} metadata
     * @throws ZboxException if any error happened
     */
    public Metadata metadata(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniMetadata(path);
    }

    /**
     * Return a vector of history versions of a regular file at specified path.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the regular file
     * @return array of file content versions
     * @throws ZboxException if any error happened
     */
    public Version[] history(String path) throws ZboxException {
        checkNullParam(path);
        return this.jniHistory(path);
    }

    /**
     * Copies the content of one file to another.
     *
     * <p>This method will overwrite the content of {@code to}.</p>
     *
     * <p>If {@code from} and {@code to} both point to the same file, this method is no-op.</p>
     *
     * <p>{@code from} and {@code to} must be absolute paths to regular files.</p>
     *
     * @param from absolute path of the source regular file
     * @param to absolute path of the target regular file
     * @throws ZboxException if any error happened
     */
    public void copy(String from, String to) throws ZboxException {
        checkNullParam2(from, to);
        this.jniCopy(from, to);
    }

    /**
     * Removes a regular file from the repository.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the regular file to be removed
     * @throws ZboxException if any error happened
     */
    public void removeFile(String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveFile(path);
    }

    /**
     * Remove an existing empty directory.
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the directory to be removed
     * @throws ZboxException if any error happened
     * @see #removeDirAll(String)
     */
    public void removeDir(String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveDir(path);
    }

    /**
     * Removes a directory at this path, after removing all its children. Use carefully!
     *
     * <p>{@code path} must be an absolute path.</p>
     *
     * @param path absolute path of the directory to be removed
     * @throws ZboxException if any error happened
     * @see #removeDir(String)
     */
    public void removeDirAll(String path) throws ZboxException {
        checkNullParam(path);
        this.jniRemoveDirAll(path);
    }

    /**
     * Rename a file or directory to a new name, replacing the original file if to already exists.
     *
     * @param from absolute path of the source regular file
     * @param to absolute path of the target regular file
     * @throws ZboxException if any error happened
     */
    public void rename(String from, String to) throws ZboxException {
        checkNullParam2(from, to);
        this.jniRename(from, to);
    }

    // jni methods
    private native static boolean jniExists(String uri) throws ZboxException;

    private native RepoInfo jniInfo();

    private native void jniResetPassword(String oldPwd, String newPwd,
                                         int opsLimit, int memLimit) throws ZboxException;

    private static native void jniRepairSuperBlock(String uri, String pwd) throws ZboxException;

    private native boolean jniPathExists(String path) throws ZboxException;

    private native boolean jniIsFile(String path) throws ZboxException;

    private native boolean jniIsDir(String path) throws ZboxException;

    private native File jniCreateFile(String path) throws ZboxException;

    private native File jniOpenFile(String path) throws ZboxException;

    private native void jniCreateDir(String path) throws ZboxException;

    private native void jniCreateDirAll(String path) throws ZboxException;

    private native DirEntry[] jniReadDir(String path) throws ZboxException;

    private native Metadata jniMetadata(String path) throws ZboxException;

    private native Version[] jniHistory(String path) throws ZboxException;

    private native void jniCopy(String from, String to) throws ZboxException;

    private native void jniRemoveFile(String path) throws ZboxException;

    private native void jniRemoveDir(String path) throws ZboxException;

    private native void jniRemoveDirAll(String path) throws ZboxException;

    private native void jniRename(String from, String to) throws ZboxException;
}
