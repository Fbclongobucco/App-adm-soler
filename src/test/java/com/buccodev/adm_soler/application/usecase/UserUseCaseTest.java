package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.user.UserRequestDto;
import com.buccodev.adm_soler.application.exception.UserAlreadyExistsException;
import com.buccodev.adm_soler.application.exception.UserNotFoundException;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserUseCase userUseCase;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository, passwordHasher);
        sampleUser = User.create("Joao Silva", "joao@email.com", "hashed-password", "11987654321");
    }

    private static UserRequestDto request(String name) {
        return new UserRequestDto(name, "joao@email.com", "password123", "11987654321");
    }

    @Test
    void createUserSavesAndReturnsIt() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordHasher.hash("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        var response = userUseCase.createUser(request("Joao Silva"));

        assertEquals("Joao Silva", response.name());
        assertEquals(User.Role.USER, response.role());
    }

    @Test
    void createUserPersistsTheHashedPasswordNeverTheRawOne() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordHasher.hash("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userUseCase.createUser(request("Joao Silva"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed-password", captor.getValue().getPassword());
    }

    @Test
    void createUserThrowsWhenEmailIsTaken() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createUser(request("Joao")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userUseCase.getUserById(id));
    }

    @Test
    void listUsersMapsThePage() {
        when(userRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleUser), 0, 20, 1, 1));

        assertEquals(1, userUseCase.listUsers(0, 20).content().size());
    }

    @Test
    void updateUserHashesTheNewPassword() {
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        when(passwordHasher.hash("password123")).thenReturn("new-hash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userUseCase.updateUser(sampleUser.getId(), request("Joao Atualizado"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("new-hash", captor.getValue().getPassword());
        assertEquals("Joao Atualizado", captor.getValue().getName());
    }

    @Test
    void deleteUserRemovesTheEntity() {
        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        userUseCase.deleteUser(sampleUser.getId());

        verify(userRepository).delete(sampleUser);
    }
}
