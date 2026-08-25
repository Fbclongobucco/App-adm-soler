package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void createDefaultsToTheLeastPrivilegedRole() {
        User user = User.create("Maria", "maria@email.com", "senha123", "11999990000");

        assertThat(user.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    void createAcceptsAnExplicitRole() {
        User user = User.create("Maria", "maria@email.com", "senha123", null, User.Role.ADMIN);

        assertThat(user.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void emailIsRequiredBecauseItIsTheLoginCredential() {
        assertThatThrownBy(() -> User.create("Maria", null, "senha123", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("email is required");
    }

    @Test
    void shortPasswordIsRejected() {
        assertThatThrownBy(() -> User.create("Maria", "maria@email.com", "123", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("at least 6 characters");
    }

    @Test
    void applyHashedPasswordSkipsRawPasswordRules() {
        User user = User.create("Maria", "maria@email.com", "senha123", null);

        // Um hash nao precisa passar pelas regras de forca de senha em claro,
        // mas nao pode ser vazio.
        user.applyHashedPassword("$2a$12$abc");

        assertThat(user.getPassword()).isEqualTo("$2a$12$abc");
        assertThatThrownBy(() -> user.applyHashedPassword("  "))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void requireStrongPasswordValidatesWithoutBuildingAUser() {
        assertThatThrownBy(() -> User.requireStrongPassword("12345"))
                .isInstanceOf(BadRequestException.class);
    }
}
