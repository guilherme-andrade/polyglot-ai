package com.polyglotai.user.infrastructure;

import com.polyglotai.user.domain.PasswordHash;
import com.polyglotai.user.domain.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt implementation of the domain {@link PasswordHasher} port.
 *
 * <p>BCrypt is a deliberately slow, salted hashing function designed for passwords. Spring
 * Security's {@link BCryptPasswordEncoder} generates a random salt per hash and embeds it in the
 * output string, so {@code matches} can verify a raw password without storing the salt separately.
 */
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public PasswordHash hash(String rawPassword) {
        return new PasswordHash(encoder.encode(rawPassword));
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        return encoder.matches(rawPassword, hash.value());
    }
}
