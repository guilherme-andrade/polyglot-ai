package com.polyglotai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Scaffold-level security configuration.
 *
 * <p>Permits unauthenticated access to the actuator health/info endpoints and the GraphQL
 * surface so feature developers can hit them in local dev. Everything else requires an
 * authenticated principal.
 *
 * <p>JWT resource-server validation is wired only when
 * {@code spring.security.oauth2.resourceserver.jwt.issuer-uri} is set. Auth feature work
 * will move this configuration into a dedicated bounded context (likely {@code user})
 * once that context is specced.
 */
@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String jwtIssuerUri;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        a -> a.requestMatchers("/actuator/health/**", "/actuator/info", "/graphql", "/graphiql/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated());

        if (!jwtIssuerUri.isBlank()) {
            http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }
}
