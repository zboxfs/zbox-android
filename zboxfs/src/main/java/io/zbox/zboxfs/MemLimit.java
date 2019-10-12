package io.zbox.zboxfs;

/**
 * Password hash memory limit.
 *
 * <p>It represents a maximum amount of memory required to perform password hashing.</p>
 *
 * <p>It is used with {@link OpsLimit}. For interactive, online operations,
 * {@link OpsLimit#INTERACTIVE} and {@link MemLimit#INTERACTIVE} provide base line for these two
 * parameters. This requires 64 MB of dedicated RAM. Higher values may improve security.</p>
 *
 * <p>Alternatively, {@link OpsLimit#MODERATE} and {@link MemLimit#MODERATE} can be used. This
 * requires 256 MB of dedicated RAM, and takes about 0.7 seconds on a 2.8 Ghz Core i7 CPU.</p>
 *
 * <p>For highly sensitive data and non-interactive operations, {@link OpsLimit#SENSITIVE} and
 * {@link MemLimit#SENSITIVE} can be used. With these parameters, deriving a key takes about 3.5
 * seconds on a 2.8 Ghz Core i7 CPU and requires 1024 MB of dedicated RAM.</p>
 *
 * <p>See
 *   <a href="https://download.libsodium.org/doc/password_hashing/the_argon2i_function" target="_blank">
 *     https://download.libsodium.org/doc/password_hashing/the_argon2i_function
 *   </a>for more details.
 * </p>
 *
 * @author Bo Lu
 * @see io.zbox.zboxfs.OpsLimit
 */
public enum MemLimit {
    INTERACTIVE(0),
    MODERATE(1),
    SENSITIVE(2);

    private final int id;

    /**
     * Create a memory limit instance with specified memory limit type.
     *
     * @param id memory limit type, e.g. {@link #INTERACTIVE}, {@link #MODERATE}, {@link #SENSITIVE}
     */
    MemLimit(int id) {
        if (id != 0 && id != 1 && id != 2) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    /**
     * Get the integer value of this memory limit.
     *
     * @return an integer value of this memory limit
     */
    public int getValue() {
        return id;
    }
}
