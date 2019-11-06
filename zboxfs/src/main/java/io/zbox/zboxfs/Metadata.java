package io.zbox.zboxfs;

/**
 * Metadata information about a regular file or a directory.
 *
 * <p>This structure is returned from the {@link File#metadata()} and {@link Repo#metadata(Path)}
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

    /**
     * Indicate whether the other metadata equals to this one.
     *
     * @param other the other metadata
     * @return {@code true} if the metadata is same, {@code false} otherwise
     */
    public boolean equals(Metadata other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        return this.fileType.equals(other.fileType)
                && this.contentLen == other.contentLen
                && this.currVersion == other.currVersion
                && this.createdAt == other.createdAt
                && this.modifiedAt == other.modifiedAt;
    }
}
