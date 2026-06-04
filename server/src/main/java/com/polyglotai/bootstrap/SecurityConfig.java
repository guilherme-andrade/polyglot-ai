package com.polyglotai.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>Two modes, switched explicitly by the {@code polyglot.security.insecure-dev} property:
 *
 * <p>The auth entry points under {@code /api/auth/**} (e.g. registration) are public in both modes
 * — they cannot require a token, since they are how a user obtains one.
 *
 * <ul>
 *   <li><strong>Secure (default, {@code insecure-dev=false})</strong>: {@code /api/auth/**} and
 *       actuator health/info are open; everything else requires an authenticated principal. JWT
 *       resource-server validation activates if
 *       {@code spring.security.oauth2.resourceserver.jwt.issuer-uri} is also configured. Without an
 *       issuer URI, protected routes simply return 403 — the application is reachable but inert
 *       until an auth feature wires up a real provider.
 *   <li><strong>Insecure dev opt-in ({@code insecure-dev=true})</strong>: additionally
 *       permits {@code /graphql} and {@code /graphiql/**} so developers can use GraphiQL
 *       without a token. The application logs a loud {@code WARN} at startup so this can
 *       never be enabled silently.
 * </ul>
 *
 * <p>This fail-closed default means an accidental production deploy without
 * {@code POLYGLOT_JWT_ISSUER_URI} returns 403 rather than exposing the GraphQL API.
 *
 * <p>This class lives in {@code com.polyglotai.bootstrap} because it does not yet belong to
 * any bounded context. The first auth feature spec will move it into the owning context
 * (likely {@code user.infrastructure}).
 */
@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String jwtIssuerUri;

    @Value("${polyglot.security.insecure-dev:false}")
    private boolean insecureDev;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (insecureDev) {
            log.warn("INSECURE DEV MODE is ON: /graphql and /graphiql are open to "
                    + "unauthenticated requests. This must be FALSE in any deployed "
                    + "environment. Unset POLYGLOT_SECURITY_INSECURE_DEV (or set it to "
                    + "false) for staging and production.");
            http.authorizeHttpRequests(a -> a.requestMatchers(
                            "/api/auth/**", "/actuator/health/**", "/actuator/info", "/graphql", "/graphiql/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());
        } else {
            http.authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/actuator/health/**", "/actuator/info")
                    .permitAll()
                    .anyRequest()
                    .authenticated());
            if (!jwtIssuerUri.isBlank()) {
                http.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
            }
        }

        return http.build();
    }
}
