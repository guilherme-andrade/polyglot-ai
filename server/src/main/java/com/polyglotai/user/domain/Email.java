package com.polyglotai.user.domain;

import java.util.regex.Pattern;

/**
 * Email address value object.
 *
 * <p>A value object is defined by its value, not an identity — two {@code Email}s with the same
 * normalised string are equal. Records give us that equality, immutability, and a constructor for
 * free.
 *
 * <p>Validation and normalisation happen in the compact constructor, so it is impossible to
 * construct an invalid {@code Email} anywhere in the system. The value is trimmed and lower-cased so
 * {@code Test@Polyglot.AI} and {@code test@polyglot.ai} are treated as the same address.
 */
public record Email(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email must not be blank");
        }
        value = value.trim().toLowerCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidEmailException("Email format is invalid");
        }
    }
}
