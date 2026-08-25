package com.buccodev.adm_soler.unit.security;

import com.buccodev.adm_soler.infra.security.SecurityUser;
import com.buccodev.adm_soler.infra.rest.entities.UserJpa;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SecurityUserTest {

    private UserJpa buildUserJpa(String role) {
        UserJpa user = new UserJpa();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("encodedPassword123");
        user.setPhone("1234567890");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    @Test
    void shouldReturnRoleWithPrefix() {
        SecurityUser securityUser = new SecurityUser(buildUserJpa("ADMIN"));

        assertThat(securityUser.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void shouldReturnUserRoles() {
        SecurityUser securityUser = new SecurityUser(buildUserJpa("USER"));

        assertThat(securityUser.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void shouldReturnForeignRoles() {
        SecurityUser securityUser = new SecurityUser(buildUserJpa("FOREIGN"));

        assertThat(securityUser.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_FOREIGN");
    }

    @Test
    void shouldReturnEmailAsUsername() {
        SecurityUser securityUser = new SecurityUser(buildUserJpa("USER"));

        assertThat(securityUser.getUsername()).isEqualTo("test@email.com");
    }

    @Test
    void shouldReturnEncodedPassword() {
        SecurityUser securityUser = new SecurityUser(buildUserJpa("USER"));

        assertThat(securityUser.getPassword()).isEqualTo("encodedPassword123");
    }

    @Test
    void shouldAlwaysBeEnabled() {
        SecurityUser securityUser = new SecurityUser(buildUserJpa("USER"));

        assertThat(securityUser.isEnabled()).isTrue();
        assertThat(securityUser.isAccountNonExpired()).isTrue();
        assertThat(securityUser.isAccountNonLocked()).isTrue();
        assertThat(securityUser.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void shouldReturnUnderlyingUser() {
        UserJpa userJpa = buildUserJpa("ADMIN");
        SecurityUser securityUser = new SecurityUser(userJpa);

        assertThat(securityUser.getUser()).isSameAs(userJpa);
    }
}
