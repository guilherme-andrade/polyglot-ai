package com.polyglotai.user.interfaces;

import com.polyglotai.user.domain.EmailAlreadyRegisteredException;
import com.polyglotai.user.domain.InvalidEmailException;
import com.polyglotai.user.domain.WeakPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates domain exceptions into HTTP status codes, matching the auth spec:
 *
 * <ul>
 *   <li>duplicate email → 409 Conflict
 *   <li>weak password / invalid email → 400 Bad Request
 * </ul>
 *
 * <p>Bean Validation failures (e.g. a blank field) are handled by Spring's default 400 response.
 */
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(EmailAlreadyRegisteredException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({WeakPasswordException.class, InvalidEmailException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidInput(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}
