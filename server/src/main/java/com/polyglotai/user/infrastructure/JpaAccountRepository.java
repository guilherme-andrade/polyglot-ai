package com.polyglotai.user.infrastructure;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link AccountEntity}.
 *
 * <p>Spring generates the implementation at runtime from the method names — {@code existsByEmail} and
 * {@code findByEmail} become SQL automatically. This is an infrastructure detail; the rest of the
 * app talks to the domain {@link com.polyglotai.user.domain.AccountRepository} port, not this
 * interface.
 */
interface JpaAccountRepository extends JpaRepository<AccountEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<AccountEntity> findByEmail(String email);
}
