package io.zbox.zboxfs;

/**
 * Information about a repository.
 *
 * <p>This structure is returned from the {@link Repo#info()} represents known metadata about a
 * repository such as its volume ID, version, URI, creation times and etc.</p>
 *
 * @author Bo Lu
 * @see Repo
 */
public class RepoInfo {
    /**
     * The 32-byte array unique volume id of this repository
     */
    public byte[] volumeId;

    /**
     * The repository version as string.
     *
     * <p>This is the string representation of the repository version, for example,
     * {@code 0.6.0}.</p>
     */
    public String version;

    /**
     * The location URI string of this repository
     */
    public String uri;

    /**
     * The repo-wise operation limit for password hash
     */
    public OpsLimit opsLimit;

    /**
     * The repo-wise memory limit for password hash
     */
    public MemLimit memLimit;

    /**
     * The repo-wise password encryption cipher
     */
    public Cipher cipher;

    /**
     * The repo-wise whether compression is enabled
     */
    public boolean compress;

    /**
     * The repo-wise maximum allowed number of file content versions
     */
    public int versionLimit;

    /**
     * The repo-wise data chunk deduplication flag
     */
    public boolean dedupChunk;

    /**
     * Whether this repository is opened as read-only
     */
    public boolean isReadOnly;

    /**
     * The creation time of this repository, in seconds from UNIX EPOCH time
     */
    public long createdAt;
}

