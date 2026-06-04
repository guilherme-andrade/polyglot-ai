package com.polyglotai.user.application;

import com.polyglotai.user.domain.Account;
import com.polyglotai.user.domain.AccountRepository;
import com.polyglotai.user.domain.Email;
import com.polyglotai.user.domain.EmailAlreadyRegisteredException;
import com.polyglotai.user.domain.PasswordHash;
import com.polyglotai.user.domain.PasswordHasher;
import com.polyglotai.user.domain.PasswordPolicy;
import java.time.Instant;

/**
 * Use case: register a new account from an email and a raw password.
 *
 * <p>This class is deliberately framework-free (no Spring annotations) — it is wired as a bean by
 * {@code com.polyglotai.user.infrastructure.UserBeanConfiguration}. It depends only on domain ports
 * ({@link AccountRepository}, {@link PasswordHasher}), so it can be unit-tested with simple fakes and
 * knows nothing about JPA, BCrypt, or HTTP.
 *
 * <p>The orchestration order matters: validate the password first (cheapest check, no I/O), then
 * normalise and uniqueness-check the email, then hash and persist.
 */
public class RegisterAccountService {

    private final AccountRepository accounts;
    private final PasswordHasher passwordHasher;

    public RegisterAccountService(AccountRepository accounts, PasswordHasher passwordHasher) {
        this.accounts = accounts;
        this.passwordHasher = passwordHasher;
    }

    public Account register(RegisterAccountCommand command) {
        PasswordPolicy.validate(command.rawPassword());

        Email email = new Email(command.email());
        if (accounts.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        PasswordHash passwordHash = passwordHasher.hash(command.rawPassword());
        Account account = Account.register(email, passwordHash, Instant.now());

        // TODO(#34 follow-up): publish a `user.account.created` domain event (ADR 0008) so the
        // curriculum context can seed a SkillProfile, and trigger the verification email. Deferred
        // until those consumers exist.
        return accounts.save(account);
    }
}
