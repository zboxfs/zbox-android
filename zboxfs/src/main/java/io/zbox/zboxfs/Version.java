package io.zbox.zboxfs;

/**
 * A representation of a permanent file content.
 *
 * @author Bo Lu
 * @see File#history()
 * @see Repo#history(Path)
 */
public class Version {
    /**
     * Version number
     */
    public long num;

    /**
     * The length of this version of content, in bytes
     */
    public long contentLen;

    /**
     * The creation time of this version of content, in seconds from UNIX EPOCH time
     */
    public long createdAt;
}

