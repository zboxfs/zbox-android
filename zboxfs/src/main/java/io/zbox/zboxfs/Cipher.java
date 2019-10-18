package io.zbox.zboxfs;

/**
 * Crypto cipher primitives.
 *
 * <p>See
 * <a href="https://download.libsodium.org/doc/secret-key_cryptography/aead" target="_blank">
 * https://download.libsodium.org/doc/secret-key_cryptography/aead
 * </a> for more details.
 * </p>
 */
public enum Cipher {
    /**
     * XChaCha20-Poly1305
     */
    XCHACHA(0),

    /**
     * AES256-GCM, hardware only
     */
    AES(1);

    private final int id;

    Cipher(int id) {
        this.id = id;
    }

    /**
     * Get the enum constant corresponding value.
     *
     * @return The enum constant corresponding integer value
     */
    public int getValue() {
        return id;
    }
}

