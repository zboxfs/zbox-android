package io.zbox.fs;

/**
 * Options and flags which can be used to configure how a file is opened.
 *
 * <p>This builder exposes the ability to configure how a {@link File} is opened and what operations
 * are permitted on the opened file. The {@link Repo#openFile(String)} and
 * {@link Repo#createFile(String)} methods are aliases for commonly used options using this builder.</p>
 *
 * <p>Generally speaking, when using {@code OpenOptions}, you'll first call {@link #OpenOptions()},
 * then chain calls to methods to set each option, then call {@link #open(Repo, String)}, passing
 * the path of the file you're trying to open. This will give you an opened {@link File} instance
 * that you can further operate on.</p>
 *
 * <h3>Example</h3>
 *
 * <p>Opening a file for both reading and writing, as well as creating it if it doesn't exist.</p>
 *
 * <blockquote><pre>
 * File file = new OpenOptions()
 *     .read(true)
 *     .write(true)
 *     .create(true)
 *     .open(repo, "/foo.txt");
 * </pre></blockquote>
 *
 * @author Bo Lu
 * @see File
 */
public class OpenOptions extends RustObject {

    private static final int rustObjId = 102;

    /**
     * Create an open option instance.
     */
    public OpenOptions() {
    }

    /**
     * Sets the option for read access.
     *
     * @param read read access flag
     * @return this open option
     */
    public OpenOptions read(boolean read) {
        this.jniRead(read);
        return this;
    }

    /**
     * Sets the option for write access.
     *
     * @param write write access flag
     * @return this open option
     */
    public OpenOptions write(boolean write) {
        this.jniWrite(write);
        return this;
    }

    /**
     * Sets the option for the append mode.
     *
     * <p>This option, when true, means that writes will append to a file instead of overwriting
     * previous content. Note that setting {@code .write(true).append(true)} has the same effect as
     * setting only {@code .append(true)}.</p>
     *
     * @param append append flag
     * @return this open option
     */
    public OpenOptions append(boolean append) {
        this.jniAppend(append);
        return this;
    }

    /**
     * Sets the option for truncating a previous file.
     *
     * <p>Note that setting {@code .write(true).truncate(true)} has the same effect as setting only
     * {@code .truncate(true)}.</p>
     *
     * @param truncate truncate flag
     * @return this open option
     */
    public OpenOptions truncate(boolean truncate) {
        this.jniTruncate(truncate);
        return this;
    }

    /**
     * Sets the option for creating a new file.
     *
     * <p>This option indicates whether a new file will be created if the file does not yet already
     * exist.</p>
     *
     * @param create create file flag
     * @return this open option
     */
    public OpenOptions create(boolean create) {
        this.jniCreate(create);
        return this;
    }

    /**
     * Sets the option to always create a new file.
     *
     * <p>This option indicates whether a new file will be created. No file is allowed to exist at
     * the target location.</p>
     *
     * @param createNew create new file flag
     * @return this open option
     */
    public OpenOptions createNew(boolean createNew) {
        this.jniCreateNew(createNew);
        return this;
    }

    /**
     * Sets the maximum number of file versions allowed.
     *
     * <p>The version limit must be within <b>[1, 255]</b>, default is <b>1</b>. It will fall back to repository's
     * {@link RepoOpener#versionLimit(int)} if it is not set.</p>
     *
     * <p>This option has no effect for directory.</p>
     *
     * @param limit limit on maximum number of file versions
     * @return this open option
     */
    public OpenOptions versionLimit(int limit) {
        this.jniVersionLimit(limit);
        return this;
    }

    /**
     * Sets the option for file data chunk deduplication.
     *
     * <p>This option indicates whether data chunk should be deduped when writing data to a file. It
     * will fall back to repository's {@link RepoOpener#dedupChunk(boolean)} if it is not set.</p>
     *
     * @param dedup dedup flag
     * @return this open option
     */
    public OpenOptions dedupChunk(boolean dedup) {
        this.jniDedupChunk(dedup);
        return this;
    }

    /**
     * Opens a file at the specified path with the options specified by this open option.
     *
     * @param repo the opened {@code Repo} instance
     * @param path absolute path of the file or directory to be opened
     * @return the opened file instance
     * @throws ZboxException if any error happened
     * @see Repo#openFile(String)
     */
    public File open(Repo repo, String path) throws ZboxException {
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

    private native File jniOpen(Repo repo, String path) throws ZboxException;
}
