package com.polyglotai.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.polyglotai.user.domain.Account;
import com.polyglotai.user.domain.AccountRepository;
import com.polyglotai.user.domain.AccountStatus;
import com.polyglotai.user.domain.Email;
import com.polyglotai.user.domain.EmailAlreadyRegisteredException;
import com.polyglotai.user.domain.PasswordHash;
import com.polyglotai.user.domain.PasswordHasher;
import com.polyglotai.user.domain.WeakPasswordException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the use case, using hand-written fakes for the domain ports. No Spring, no database,
 * no BCrypt — this is the payoff of defining {@link AccountRepository} and {@link PasswordHasher} as
 * interfaces in the domain.
 */
class RegisterAccountServiceTest {

    /** In-memory stand-in for the real JPA-backed repository. */
    private static final class InMemoryAccountRepository implements AccountRepository {
        private final Map<String, Account> byEmail = new HashMap<>();

        @Override
        public boolean existsByEmail(Email email) {
            return byEmail.containsKey(email.value());
        }

        @Override
        public Account save(Account account) {
            byEmail.put(account.email().value(), account);
            return account;
        }

        @Override
        public Optional<Account> findByEmail(Email email) {
            return Optional.ofNullable(byEmail.get(email.value()));
        }
    }

    /** Deterministic stand-in for BCrypt — just prefixes the raw password. */
    private static final class FakePasswordHasher implements PasswordHasher {
        @Override
        public PasswordHash hash(String rawPassword) {
            return new PasswordHash("hashed:" + rawPassword);
        }

        @Override
        public boolean matches(String rawPassword, PasswordHash hash) {
            return hash.value().equals("hashed:" + rawPassword);
        }
    }

    private final AccountRepository accounts = new InMemoryAccountRepository();
    private final RegisterAccountService service = new RegisterAccountService(accounts, new FakePasswordHasher());

    @Test
    void registersANewAccountWithNormalisedEmailAndHashedPassword() {
        Account account = service.register(new RegisterAccountCommand("New@Polyglot.AI", "secret123"));

        assertThat(account.email().value()).isEqualTo("new@polyglot.ai");
        assertThat(account.status()).isEqualTo(AccountStatus.PENDING_VERIFICATION);
        assertThat(account.passwordHash().value()).isEqualTo("hashed:secret123");
        assertThat(accounts.findByEmail(new Email("new@polyglot.ai"))).isPresent();
    }

    @Test
    void rejectsDuplicateEmail() {
        service.register(new RegisterAccountCommand("dupe@polyglot.ai", "secret123"));

        assertThatThrownBy(() -> service.register(new RegisterAccountCommand("dupe@polyglot.ai", "another123")))
                .isInstanceOf(EmailAlreadyRegisteredException.class)
                .hasMessage("An account with this email already exists");
    }

    @Test
    void rejectsWeakPasswordBeforeTouchingTheRepository() {
        assertThatThrownBy(() -> service.register(new RegisterAccountCommand("x@y.co", "12345678")))
                .isInstanceOf(WeakPasswordException.class);

        assertThat(accounts.findByEmail(new Email("x@y.co"))).isEmpty();
    }
}
