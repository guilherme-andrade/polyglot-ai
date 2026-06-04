package com.polyglotai.user.domain;

/**
 * Thrown when registration is attempted with an email that already has an account.
 *
 * <p>The message is the exact user-facing text the auth spec requires for the 409 response.
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException() {
        super("An account with this email already exists");
    }
}
