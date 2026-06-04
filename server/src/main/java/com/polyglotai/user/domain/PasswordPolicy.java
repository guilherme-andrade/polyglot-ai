package com.polyglotai.user.domain;

/**
 * The password strength rules from the auth spec: at least 8 characters, with at least one letter
 * and one digit.
 *
 * <p>This is a domain policy expressed as a small stateless helper. Keeping the rule here (rather
 * than as validation annotations on a DTO) means it is unit-testable without any framework and
 * reused by every entry point that creates or changes a password.
 */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 8;

    private PasswordPolicy() {}

    /**
     * Validates a raw (plaintext) password.
     *
     * @throws WeakPasswordException if the password violates any rule
     */
    public static void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH) {
            throw new WeakPasswordException("Password must be at least " + MIN_LENGTH + " characters long");
        }
        if (rawPassword.chars().noneMatch(Character::isLetter)) {
            throw new WeakPasswordException("Password must contain at least one letter");
        }
        if (rawPassword.chars().noneMatch(Character::isDigit)) {
            throw new WeakPasswordException("Password must contain at least one number");
        }
    }
}
