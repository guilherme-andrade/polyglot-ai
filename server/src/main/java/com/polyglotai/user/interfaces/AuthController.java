package com.polyglotai.user.interfaces;

import com.polyglotai.user.application.RegisterAccountCommand;
import com.polyglotai.user.application.RegisterAccountService;
import com.polyglotai.user.domain.Account;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for authentication. Auth flows use REST (not GraphQL) per ADR 0002.
 *
 * <p>The controller is a thin boundary: it accepts a validated request DTO, delegates to the
 * application service, and maps the returned domain {@link Account} into a response DTO. No business
 * logic lives here.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterAccountService registerAccountService;

    public AuthController(RegisterAccountService registerAccountService) {
        this.registerAccountService = registerAccountService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        Account account =
                registerAccountService.register(new RegisterAccountCommand(request.email(), request.password()));
        return AccountResponse.from(account);
    }
}
