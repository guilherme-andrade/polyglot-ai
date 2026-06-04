package com.polyglotai.user.infrastructure;

import com.polyglotai.user.application.RegisterAccountService;
import com.polyglotai.user.domain.AccountRepository;
import com.polyglotai.user.domain.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the framework-free application services of the {@code user} context as Spring beans.
 *
 * <p>The application layer carries no Spring annotations (so it stays trivially unit-testable), so
 * the wiring happens here in infrastructure instead. Spring injects the {@link AccountRepository} and
 * {@link PasswordHasher} beans — provided by {@link AccountRepositoryAdapter} and
 * {@link BCryptPasswordHasher} — into the constructed service.
 */
@Configuration
class UserBeanConfiguration {

    @Bean
    RegisterAccountService registerAccountService(AccountRepository accountRepository, PasswordHasher passwordHasher) {
        return new RegisterAccountService(accountRepository, passwordHasher);
    }
}
