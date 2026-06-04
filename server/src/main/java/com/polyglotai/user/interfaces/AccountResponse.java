package com.polyglotai.user.interfaces;

import com.polyglotai.user.domain.Account;
import java.util.UUID;

/**
 * Response body describing a created account.
 *
 * <p>This DTO is the public shape of an account over HTTP. It deliberately omits the password hash —
 * the domain {@link Account} is mapped into this DTO so internal fields never leak into the API.
 */
public record AccountResponse(UUID id, String email, String status) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.id(), account.email().value(), account.status().name());
    }
}
