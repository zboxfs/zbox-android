package io.zbox.fs;

/**
 * A builder used to create a repository in various manners.
 *
 * <p>This builder exposes the ability to configure how a {@link Repo} is opened and what operations
 * are permitted on the opened repository.</p>
 *
 * <p>Generally speaking, when using {@code RepoOpener}, you'll first call {@link #RepoOpener()},
 * then chain calls to methods to set each option, then call {@link #open(String, String)}, passing
 * the URI of the repository and password you're trying to open. This will give you an opened
 * {@link Repo} instance that you can further operate on.</p>
 *
 * <h3>Examples</h3>
 *
 * <p>Opening a repository and creating it if it doesn't exist.</p>
 *
 * <blockquote><pre>
 * Repo repo = new RepoOpener().create(true).open("mem://foo", "pwd");
 * </pre></blockquote>
 *
 * <p>Specify options for creating a repository.</p>
 *
 * <blockquote><pre>
 * Repo repo = new RepoOpener()
 *     .opsLimit(OpsLimit.MODERATE)
 *     .memLimit(MemLimit.INTERACTIVE)
 *     .create(true)
 *     .open("mem://foo", "pwd");
 * </pre></blockquote>
 *
 * @author Bo Lu
 * @see Repo
 */
public class RepoOpener extends RustObject {

    private static final int rustObjId = 100;

    /**
     * Create a repo opener instance.
     */
    public RepoOpener() {
    }

    /**
     * Sets the password hash operation limit.
     *
     * <p>This option is only used for creating a repository. {@link OpsLimit#INTERACTIVE} is the
     * default.</p>
     *
     * @param limit password hash operation limit
     * @return this repo opener
     */
    public RepoOpener opsLimit(OpsLimit limit) {
        OpsLimit lim = limit == null ? OpsLimit.INTERACTIVE : limit;
        this.jniOpsLimit(lim.getValue());
        return this;
    }

    /**
     * Sets the password hash memory limit.
     *
     * <p>This option is only used for creating a repository. {@link MemLimit#INTERACTIVE} is the
     * default.</p>
     *
     * @param limit password hash memory limit
     * @return this repo opener
     */
    public RepoOpener memLimit(MemLimit limit) {
        MemLimit lim = limit == null ? MemLimit.INTERACTIVE : limit;
        this.jniMemLimit(lim.getValue());
        return this;
    }

    /**
     * Sets the crypto cipher encrypts the repository.
     *
     * <p>This option is only used for creating a repository. {@link Cipher#AES} is the default if
     * CPU supports AES-NI instructions, otherwise it will fall back to {@link Cipher#XCHACHA}.</p>
     *
     * @param cipher crypto cipher
     * @return this repo opener
     */
    public RepoOpener cipher(Cipher cipher) {
        Cipher ci = cipher == null ? Cipher.XCHACHA : cipher;
        this.jniCipher(ci.getValue());
        return this;
    }

    /**
     * Sets the option for creating a new repository.
     *
     * <p>This option indicates whether a new repository will be created if the repository does not
     * yet already exist.</p>
     *
     * @param create create repo flag
     * @return this repo opener
     */
    public RepoOpener create(boolean create) {
        this.jniCreate(create);
        return this;
    }

    /**
     * Sets the option for creating a new repository.
     *
     * <p>This option indicates whether a new repository will be created. No repository is allowed
     * to exist at the target path.</p>
     *
     * @param createNew create new repo flag
     * @return this repo opener
     */
    public RepoOpener createNew(boolean createNew) {
        this.jniCreateNew(createNew);
        return this;
    }

    /**
     * Sets the option for data compression.
     *
     * <p>This options indicates whether the LZ4 compression should be used in the repository.
     * Default is false.</p>
     *
     * @param compress compress flag
     * @return this repo opener
     */
    public RepoOpener compress(boolean compress) {
        this.jniCompress(compress);
        return this;
    }

    /**
     * Sets the repo-wise maximum number of file content versions.
     *
     * <p>The version limit must be within <b>[1, 255]</b>, default is <b>1</b>. This setting is a
     * repository-wise setting, individual file can overwrite it by using
     * {@link OpenOptions#versionLimit(int)}.</p>
     *
     * @param limit maximum number of file content versions
     * @return this repo opener
     */
    public RepoOpener versionLimit(int limit) {
        this.jniVersionLimit(limit);
        return this;
    }

    /**
     * Sets the repo-wise file data chunk deduplication flag.
     *
     * <p>This option indicates whether data chunk should be deduped when writing data to a file.
     * This setting is a repository-wise setting, individual file can overwrite it by using
     * {@link OpenOptions#dedupChunk(boolean)}. Default is {@code false}.</p>
     *
     * @param dedup dedup flag
     * @return this repo opener
     */
    public RepoOpener dedupChunk(boolean dedup) {
        this.jniDedupChunk(dedup);
        return this;
    }

    /**
     * Sets the option for read-only mode.
     *
     * @param readOnly read only flag
     * @return this repo opener
     */
    public RepoOpener readOnly(boolean readOnly) {
        this.jniReadOnly(readOnly);
        return this;
    }

    /**
     * Sets the option to open repo regardless repo lock.
     *
     * <p>Normally, repo will be exclusively locked once it is opened. But when this option is set
     * to true, the repo will be opened regardless the repo lock. This option breaks exclusive
     * access to repo, so use it cautiously. Default is {@code false}.</p>
     *
     * @param force force open flag
     * @return this repo opener
     */
    public RepoOpener force(boolean force) {
        this.jniForce(force);
        return this;
    }

    /**
     * Opens a repository at URI with the password and options specified by this repo opener.
     *
     * <p>In general, the URI is structured as follows:</p>
     *
     * <blockquote><pre>
     * storage://username:password@/path/data?key=value&key2=value2
     * |------| |-----------------||---------||-------------------|
     *    |              |              |               |
     * identifier    authority         path         parameters
     * </pre></blockquote>
     *
     * <p>Only {@code identifier} and {@code path} are required, all the others are optional.</p>
     *
     * <p>Supported storage:</p>
     *
     * <ul>
     *     <li>
     *         Memory storage, URI identifier is <b>mem://</b>
     *         <p>After the identifier is a name to distinguish a particular memory storage
     *         location.</p>
     *         <p>For example, <i>mem://foobar</i>.</p>
     *     </li>
     *     <li>
     *         OS file system storage, URI identifier is <b>file://</b>
     *         <p>After the identifier is the path to a directory on OS file system. It can be a
     *         relative or absolute path.</p>
     *         <p>For example, <i>file://./foo/bar</i>.</p>
     *     </li>
     *     <li>
     *         Zbox Cloud Storage, URI identifier is <b>zbox://</b>
     *         <p>
     *             Visit <a href="https://zbox.io">zbox.io</a> to learn more about Zbox Cloud
     *             Storage.
     *         </p>
     *     </li>
     * </ul>
     *
     * @param uri the repo's location URI
     * @param pwd the password to encrypt repo
     * @return the opened repo instance
     * @throws ZboxException if any error happened
     */
    public Repo open(String uri, String pwd) throws ZboxException {
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

    private native Repo jniOpen(String uri, String pwd) throws ZboxException;
}

