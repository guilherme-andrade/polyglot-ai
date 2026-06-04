package com.polyglotai.user.domain;

/** Thrown when a raw password fails {@link PasswordPolicy}. */
public class WeakPasswordException extends RuntimeException {

    public WeakPasswordException(String message) {
        super(message);
    }
}
