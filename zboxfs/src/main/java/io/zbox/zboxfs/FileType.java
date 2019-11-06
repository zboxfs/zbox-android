package io.zbox.zboxfs;

/**
 * This class represents a type of file, that is, a regular file or a directory.
 *
 * @author Bo Lu
 * @see File
 */
public enum FileType {
    /**
     * Regular file
     */
    FILE(0),

    /**
     * Directory
     */
    DIR(1);

    private final int id;

    /**
     * Create a file type instance with specified file type.
     *
     * @param id file type, either {@code FileType.FILE} or {@code FileType.DIR}
     */
    FileType(int id) {
        if (id != 0 && id != 1) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    /**
     * Get the integer value of this file type.
     *
     * @return an integer value of this file type
     */
    public int getValue() {
        return id;
    }

    /**
     * Indicate whether the other file type equals to this one.
     *
     * @param other the other file type
     * @return {@code true} if the file type is same, {@code false} otherwise
     */
    public boolean equals(FileType other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        return this.id == other.id;
    }
}

