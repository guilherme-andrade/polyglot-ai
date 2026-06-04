package com.polyglotai.user.domain;

/**
 * Port for hashing and verifying passwords.
 *
 * <p>The domain declares <em>what</em> it needs (turn a raw password into a {@link PasswordHash},
 * and check a raw password against one) without knowing <em>how</em>. The concrete BCrypt
 * implementation lives in the infrastructure layer, so the security library stays out of the domain.
 * This is the Dependency Inversion Principle: the domain owns the interface, infrastructure provides
 * the implementation.
 */
public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);
}
