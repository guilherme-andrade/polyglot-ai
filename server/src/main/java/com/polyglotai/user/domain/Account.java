package com.polyglotai.user.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * The {@code Account} aggregate root for the {@code user} context.
 *
 * <p>An aggregate is the unit of consistency: all changes to a user's account go through this class,
 * which enforces the invariants (e.g. an account always has a valid email and a password hash, and
 * starts life pending verification).
 *
 * <p>Two factory methods exist on purpose:
 *
 * <ul>
 *   <li>{@link #register} — creates a <em>new</em> account, generating a fresh id and setting the
 *       initial status. Use this when a user signs up.
 *   <li>{@link #reconstitute} — rebuilds an <em>existing</em> account from stored state. Only the
 *       infrastructure layer should call this when mapping a database row back into the domain.
 * </ul>
 */
public class Account {

    private final UUID id;
    private final Email email;
    private final PasswordHash passwordHash;
    private AccountStatus status;
    private final Instant createdAt;

    private Account(UUID id, Email email, PasswordHash passwordHash, AccountStatus status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    /** Registers a brand-new account in the {@link AccountStatus#PENDING_VERIFICATION} state. */
    public static Account register(Email email, PasswordHash passwordHash, Instant createdAt) {
        return new Account(UUID.randomUUID(), email, passwordHash, AccountStatus.PENDING_VERIFICATION, createdAt);
    }

    /** Rebuilds an account from persisted state. For infrastructure mapping only. */
    public static Account reconstitute(
            UUID id, Email email, PasswordHash passwordHash, AccountStatus status, Instant createdAt) {
        return new Account(id, email, passwordHash, status, createdAt);
    }

    /** Marks the account active once its email has been verified. */
    public void activate() {
        this.status = AccountStatus.ACTIVE;
    }

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public AccountStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
