package io.zbox.zboxfs;

/**
 * Enumeration of possible methods to seek within a file or version reader.
 *
 * <p>It is used by the {@link File#seek(long, SeekFrom)} and
 * {@link VersionReader#seek(long, SeekFrom)} methods.</p>
 *
 * @author Bo Lu
 * @see File
 * @see VersionReader
 */
public enum SeekFrom {
    /**
     * Seek start from the beginning of file
     */
    START(0),

    /**
     * Seek start from current position of file
     */
    CURRENT(1),

    /**
     * Seek start from the end of file
     */
    END(2);

    private final int id;

    /**
     * Create a SeekFrom instance with specified value.
     *
     * @param id operation limit type, e.g. {@link #START}, {@link #CURRENT}, {@link #END}
     */
    SeekFrom(int id) {
        if (id != 0 && id != 1 && id != 2) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    /**
     * Get the integer value of this SeekFrom instance.
     *
     * @return an integer value of this SeekFrom instance
     */
    public int getValue() {
        return id;
    }
}
