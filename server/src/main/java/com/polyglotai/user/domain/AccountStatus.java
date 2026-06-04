package com.polyglotai.user.domain;

/** Lifecycle state of an {@link Account}. */
public enum AccountStatus {

    /** Created but the email has not been verified yet. */
    PENDING_VERIFICATION,

    /** Email verified; the account can be used normally. */
    ACTIVE,

    /** Administratively disabled. */
    SUSPENDED
}
