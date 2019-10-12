package io.zbox.zboxfs;

/**
 * Metadata information about a regular file or a directory.
 *
 * <p>This structure is returned from the {@link File#metadata()} and {@link Repo#metadata(String)}
 * represents known metadata about a regular file or a directory, such as its type, size,
 * modification times, etc.</p>
 *
 * @author Bo Lu
 */
public class Metadata {
    /**
     * File type of this metadata
     */
    public FileType fileType;

    /**
     * Content length, in bytes
     */
    public long contentLen;

    /**
     * Current version number of file listed in this metadata
     */
    public int currVersion;

    /**
     * The creation time listed in this metadata, in seconds from UNIX EPOCH time
     */
    public long createdAt;

    /**
     * The last modification time listed in this metadata, in seconds from UNIX EPOCH time
     */
    public long modifiedAt;

    /**
     * Return if this metadata represents a directory.
     *
     * @return true if this is a directory, false otherwise
     * @see #isFile()
     */
    public boolean isDir() {
        return this.fileType == FileType.DIR;
    }

    /**
     * Return if this metadata represents a regular file.
     *
     * @return true if this is a regular file, false otherwise
     * @see #isDir()
     */
    public boolean isFile() {
        return this.fileType == FileType.FILE;
    }
}
