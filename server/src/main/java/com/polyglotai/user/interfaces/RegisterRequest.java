package com.polyglotai.user.interfaces;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for {@code POST /api/auth/register}.
 *
 * <p>Validation here is intentionally shallow — just "not blank". The real rules (email format,
 * password strength) live in the domain ({@link com.polyglotai.user.domain.Email} and
 * {@link com.polyglotai.user.domain.PasswordPolicy}) so they are enforced no matter which entry point
 * creates an account, and are unit-testable without HTTP.
 */
public record RegisterRequest(
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password) {}
