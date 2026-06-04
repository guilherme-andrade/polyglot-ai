package com.polyglotai.user.infrastructure;

import com.polyglotai.user.domain.Account;
import com.polyglotai.user.domain.AccountRepository;
import com.polyglotai.user.domain.Email;
import com.polyglotai.user.domain.PasswordHash;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements the domain {@link AccountRepository} port using Spring Data JPA.
 *
 * <p>This is the "glue" of the ports-and-adapters pattern: the domain defines the interface, this
 * class fulfils it by delegating to {@link JpaAccountRepository} and mapping between the domain
 * {@link Account} and the persistence {@link AccountEntity}.
 */
@Component
public class AccountRepositoryAdapter implements AccountRepository {

    private final JpaAccountRepository jpa;

    public AccountRepositoryAdapter(JpaAccountRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.value());
    }

    @Override
    public Account save(Account account) {
        AccountEntity saved = jpa.save(toEntity(account));
        return toDomain(saved);
    }

    @Override
    public Optional<Account> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(this::toDomain);
    }

    private AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.id(),
                account.email().value(),
                account.passwordHash().value(),
                account.status(),
                account.createdAt());
    }

    private Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                entity.getId(),
                new Email(entity.getEmail()),
                new PasswordHash(entity.getPasswordHash()),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
