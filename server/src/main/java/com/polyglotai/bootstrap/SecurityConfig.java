package com.polyglotai.bootstrap;

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
 * <p>Two modes, switched by whether {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}
 * is configured:
 *
 * <ul>
 *   <li><strong>Unconfigured (local dev)</strong>: actuator health/info, {@code /graphql}, and
 *       {@code /graphiql/**} are open so developers can hit GraphiQL without a token. Everything
 *       else requires authentication, but no auth mechanism is wired up, so protected routes
 *       return 403 until the first auth feature lands.
 *   <li><strong>Configured (staging / prod)</strong>: only actuator health/info are open.
 *       {@code /graphql} requires a valid JWT validated against the configured issuer; GraphiQL
 *       is not exposed.
 * </ul>
 *
 * <p>This class is in {@code com.polyglotai.bootstrap} because it does not yet belong to any
 * bounded context. The first auth feature spec will move it into the owning context
 * (likely {@code user.infrastructure}).
 */
@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String jwtIssuerUri;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (jwtIssuerUri.isBlank()) {
            // Local dev: GraphQL and GraphiQL are open so developers can iterate without a token.
            http.authorizeHttpRequests(
                    a -> a.requestMatchers("/actuator/health/**", "/actuator/info", "/graphql", "/graphiql/**")
                            .permitAll()
                            .anyRequest()
                            .authenticated());
        } else {
            // Configured environment: GraphQL requires a valid JWT. Only health/info stay open.
            http.authorizeHttpRequests(a -> a.requestMatchers("/actuator/health/**", "/actuator/info")
                            .permitAll()
                            .anyRequest()
                            .authenticated())
                    .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }
}
