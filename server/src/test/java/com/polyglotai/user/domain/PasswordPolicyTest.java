package com.polyglotai.user.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PasswordPolicyTest {

    @Test
    void acceptsAPasswordWithLettersDigitsAndEnoughLength() {
        assertThatCode(() -> PasswordPolicy.validate("secret123")).doesNotThrowAnyException();
    }

    @Test
    void rejectsTooShort() {
        assertThatThrownBy(() -> PasswordPolicy.validate("ab1"))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessageContaining("8 characters");
    }

    @Test
    void rejectsPasswordWithoutALetter() {
        assertThatThrownBy(() -> PasswordPolicy.validate("12345678"))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessageContaining("letter");
    }

    @Test
    void rejectsPasswordWithoutADigit() {
        assertThatThrownBy(() -> PasswordPolicy.validate("abcdefgh"))
                .isInstanceOf(WeakPasswordException.class)
                .hasMessageContaining("number");
    }
}
