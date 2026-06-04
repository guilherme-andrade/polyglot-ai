package com.polyglotai.user.domain;

import java.util.Optional;

/**
 * Port for persisting and looking up {@link Account}s.
 *
 * <p>Like {@link PasswordHasher}, this interface is owned by the domain and implemented in
 * infrastructure (by a JPA-backed adapter). The application layer depends only on this interface, so
 * the use-case logic never knows whether accounts live in PostgreSQL, an in-memory map (handy for
 * tests), or anything else.
 */
public interface AccountRepository {

    boolean existsByEmail(Email email);

    Account save(Account account);

    Optional<Account> findByEmail(Email email);
}
