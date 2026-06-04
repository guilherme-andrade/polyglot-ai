package com.polyglotai.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void normalisesToLowercaseAndTrimsWhitespace() {
        assertThat(new Email("  Test@Polyglot.AI  ").value()).isEqualTo("test@polyglot.ai");
    }

    @Test
    void acceptsAValidAddress() {
        assertThat(new Email("a@b.co").value()).isEqualTo("a@b.co");
    }

    @Test
    void rejectsBlank() {
        assertThatThrownBy(() -> new Email("   ")).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new Email(null)).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void rejectsStringWithoutAtSign() {
        assertThatThrownBy(() -> new Email("not-an-email")).isInstanceOf(InvalidEmailException.class);
    }
}
