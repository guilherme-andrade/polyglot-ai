package com.polyglotai;

import org.junit.jupiter.api.Test;

/**
 * Smoke test: verifies the full Spring application context starts. Now that the {@code user} context
 * persists to PostgreSQL, this boots against a real Testcontainers database (see
 * {@link AbstractPostgresIntegrationTest}).
 */
class PolyglotAiApplicationTests extends AbstractPostgresIntegrationTest {

    @Test
    void contextLoads() {}
}
