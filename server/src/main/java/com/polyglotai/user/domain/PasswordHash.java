package com.polyglotai.user.domain;

/**
 * A one-way hash of a password. Wrapping the hash string in a value object makes it impossible to
 * accidentally pass a raw password where a hash is expected (the types differ), and documents intent
 * at every call site.
 */
public record PasswordHash(String value) {

    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }
    }
}
