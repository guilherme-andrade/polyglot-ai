package com.polyglotai.user.infrastructure;

import com.polyglotai.user.domain.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA mapping for the {@code user_account} table.
 *
 * <p>This is intentionally separate from the domain {@link com.polyglotai.user.domain.Account}: the
 * entity is a persistence concern shaped by JPA annotations, while the aggregate is a behaviour-rich
 * domain object. The {@link AccountRepositoryAdapter} translates between the two. Keeping them apart
 * means the database schema can evolve without forcing changes on the domain model.
 *
 * <p>JPA requires a no-arg constructor (used reflectively); it is {@code protected} so application
 * code uses the real constructor instead.
 */
@Entity
@Table(name = "user_account")
class AccountEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AccountEntity() {}

    AccountEntity(UUID id, String email, String passwordHash, AccountStatus status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
    }

    UUID getId() {
        return id;
    }

    String getEmail() {
        return email;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    AccountStatus getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
