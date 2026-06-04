package com.polyglotai;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for tests that need the full Spring context backed by a real PostgreSQL database.
 *
 * <p>Uses the Testcontainers "singleton container" pattern: the container is a {@code static} field
 * started once in a static initialiser and reused across every subclass and test method in the JVM.
 * This is much faster than starting a fresh database per test class.
 *
 * <p>The image is {@code pgvector/pgvector:pg17} (not plain {@code postgres}) because Flyway's
 * {@code V1__init.sql} enables the {@code vector} extension, which only that image ships.
 *
 * <p>Requires a running Docker daemon.
 */
@SpringBootTest
public abstract class AbstractPostgresIntegrationTest {

    @SuppressWarnings("resource") // lives for the whole JVM; Testcontainers' Ryuk stops it at exit
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("pgvector/pgvector:pg17")
            .withDatabaseName("polyglot")
            .withUsername("polyglot")
            .withPassword("polyglot");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
