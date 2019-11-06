package io.zbox.zboxfs;

/**
 * Entries returned by the {@link Repo#readDir(Path)} function.
 *
 * <p>An instance of {@code DirEntry} represents an entry inside of a directory in the repository.
 * Each entry can be inspected via methods to learn about the full path or other metadata.</p>
 *
 * @author Bo Lu
 */
public class DirEntry {
    /**
     * The full path to the file that this entry represents.
     */
    public Path path;

    /**
     * The bare file name of this directory entry without any other leading path component.
     */
    public String fileName;

    /**
     * the metadata for the file that this entry points at.
     */
    public Metadata metadata;

    /**
     * Indicate whether the other entry equals to this one.
     *
     * @param other the other entry
     * @return {@code true} if the entry is same, {@code false} otherwise
     */
    public boolean equals(DirEntry other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        return this.path.equals(other.path)
                && this.fileName.equals(other.fileName)
                && this.metadata.equals(other.metadata);
    }
}