package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidUserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static User valid() {
        return User.create("Joao Silva", "joao@email.com", "hashed-password", "11987654321");
    }

    @Test
    void createsUserAsRegularUser() {
        User user = valid();

        assertNotNull(user.getId());
        assertEquals(User.Role.USER, user.getRole());
        assertFalse(user.isAdmin());
    }

    @Test
    void createAdminGetsAdminRole() {
        User admin = User.createAdmin("Admin", "admin@soler.com", "hashed", "11987654321");

        assertEquals(User.Role.ADMIN, admin.getRole());
        assertTrue(admin.isAdmin());
    }

    @Test
    void throwsWhenNameIsBlank() {
        assertThrows(InvalidUserException.class,
                () -> User.create(" ", "joao@email.com", "hashed", "11987654321"));
    }

    @Test
    void throwsWhenEmailIsMalformed() {
        assertThrows(InvalidUserException.class,
                () -> User.create("Joao", "not-an-email", "hashed", "11987654321"));
    }

    @Test
    void throwsWhenPasswordIsBlank() {
        assertThrows(InvalidUserException.class,
                () -> User.create("Joao", "joao@email.com", "  ", "11987654321"));
    }

    @Test
    void throwsWhenPhoneHasWrongLength() {
        assertThrows(InvalidUserException.class,
                () -> User.create("Joao", "joao@email.com", "hashed", "123"));
    }

    @Test
    void allowsNullPhone() {
        assertNull(User.create("Joao", "joao@email.com", "hashed", null).getPhone());
    }

    @Test
    void updateKeepsRoleAndReplacesTheRest() {
        User user = valid();

        user.update("Joao Atualizado", "novo@email.com", "novo-hash", "11912345678");

        assertEquals("Joao Atualizado", user.getName());
        assertEquals("novo@email.com", user.getEmail());
        assertEquals("novo-hash", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    void updateRejectsInvalidEmailAndKeepsPreviousState() {
        User user = valid();

        assertThrows(InvalidUserException.class,
                () -> user.update("Joao", "quebrado", "hash", "11987654321"));
        assertEquals("joao@email.com", user.getEmail());
    }

    @Test
    void equalsIsBasedOnId() {
        User user = valid();
        User restored = User.restore(user.getId(), "Outro", "outro@email.com", "h", null,
                User.Role.ADMIN, user.getCreatedAt(), user.getUpdatedAt());

        assertEquals(user, restored);
    }
}
