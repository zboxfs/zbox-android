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
}