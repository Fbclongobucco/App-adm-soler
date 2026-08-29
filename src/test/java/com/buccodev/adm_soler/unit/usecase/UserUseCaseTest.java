package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.UserUseCase;
import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserUseCase userUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.restore(
                UUID.randomUUID(),
                "Joao Silva",
                "joao@email.com",
                "encodedPass123",
                "1234567890",
                User.Role.USER,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateUser() {
        UserRequest request = new UserRequest("Joao Silva", "joao@email.com", "password123", "1234567890");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = userUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Joao Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        UserResponse response = userUseCase.findById(sampleUser.getId());

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(sampleUser.getId());
        assertThat(response.name()).isEqualTo("Joao Silva");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario nao encontrado");
    }

    @Test
    void shouldFindAllUsers() {
        User anotherUser = User.restore(
                UUID.randomUUID(), "Maria", "maria@email.com", "encodedPass",
                "0987654321", User.Role.ADMIN,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
        when(userRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleUser, anotherUser), 0, 20, 2, 1));

        PageResponse<UserResponse> responses = userUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(2);
        verify(userRepository).findAll(any(PageQuery.class));
    }

    @Test
    void shouldUpdateUser() {
        UserRequest request = new UserRequest("Joao Updated", "joao@email.com", "newpass123", "1234567890");
        User updatedUser = User.restore(
                sampleUser.getId(), "Joao Updated", "joao@email.com", "encodedNewPass",
                "1234567890", User.Role.USER,
                sampleUser.getCreatedAt(), java.time.LocalDateTime.now()
        );
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = userUseCase.update(sampleUser.getId(), request);

        assertThat(response.name()).isEqualTo("Joao Updated");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        UserRequest request = new UserRequest("Test", "test@email.com", "pass123", null);
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userUseCase.update(nonExistentId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(sampleUser.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(sampleUser.getId());

        userUseCase.delete(sampleUser.getId());

        verify(userRepository).deleteById(sampleUser.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> userUseCase.delete(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
