package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.auth.LoginRequestDto;
import com.buccodev.adm_soler.application.dto.auth.TokenResponseDto;
import com.buccodev.adm_soler.application.exception.InvalidCredentialsException;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import com.buccodev.adm_soler.core.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenService tokenService;

    private AuthUseCase authUseCase;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        authUseCase = new AuthUseCase(userRepository, passwordHasher, tokenService);
        sampleUser = User.create("Joao Silva", "joao@email.com", "hashed-password", "11987654321");
    }

    @Test
    void loginReturnsBothTokens() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(passwordHasher.matches("password123", "hashed-password")).thenReturn(true);
        when(tokenService.generateAccessToken(sampleUser)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(sampleUser)).thenReturn("refresh-token");

        TokenResponseDto response = authUseCase.login(
                new LoginRequestDto("joao@email.com", "password123"));

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(passwordHasher.matches("wrong", "hashed-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authUseCase.login(new LoginRequestDto("joao@email.com", "wrong")));
        verifyNoInteractions(tokenService);
    }

    @Test
    void loginThrowsWhenEmailIsUnknown() {
        when(userRepository.findByEmail("ninguem@email.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authUseCase.login(new LoginRequestDto("ninguem@email.com", "password123")));
    }

    @Test
    void refreshIssuesANewAccessTokenAndKeepsTheRefreshToken() {
        when(tokenService.extractEmailIfValidRefreshToken("refresh-token"))
                .thenReturn(Optional.of("joao@email.com"));
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(tokenService.generateAccessToken(sampleUser)).thenReturn("new-access-token");

        TokenResponseDto response = authUseCase.refresh("refresh-token");

        assertEquals("new-access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void refreshThrowsWhenTokenIsNotAValidRefreshToken() {
        when(tokenService.extractEmailIfValidRefreshToken("access-token"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authUseCase.refresh("access-token"));
    }

    @Test
    void refreshThrowsWhenTheUserIsGone() {
        when(tokenService.extractEmailIfValidRefreshToken("refresh-token"))
                .thenReturn(Optional.of("joao@email.com"));
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authUseCase.refresh("refresh-token"));
    }
}
