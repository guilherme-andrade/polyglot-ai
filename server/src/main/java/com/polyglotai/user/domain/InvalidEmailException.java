package com.polyglotai.user.domain;

/** Thrown when an email string does not satisfy {@link Email}'s format rules. */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
