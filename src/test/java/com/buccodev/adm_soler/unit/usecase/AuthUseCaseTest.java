package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.auth.AuthRequest;
import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.RefreshTokenRequest;
import com.buccodev.adm_soler.application.dto.auth.RegisterRequest;
import com.buccodev.adm_soler.application.exception.AuthenticationException;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.gateway.AuthenticationGatewayPort;
import com.buccodev.adm_soler.application.gateway.PasswordEncoderPort;
import com.buccodev.adm_soler.application.gateway.TokenProviderPort;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private AuthenticationGatewayPort authenticationGateway;

    @Mock
    private TokenProviderPort tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private AuthUseCase authUseCase;

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
    void shouldLoginSuccessfully() {
        AuthRequest request = new AuthRequest("joao@email.com", "password123");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(tokenProvider.generateAccessToken("joao@email.com")).thenReturn("access-token-123");
        when(tokenProvider.generateRefreshToken("joao@email.com")).thenReturn("refresh-token-123");

        AuthResponse response = authUseCase.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token-123");
        assertThat(response.refreshToken()).isEqualTo("refresh-token-123");
        assertThat(response.email()).isEqualTo("joao@email.com");
        assertThat(response.name()).isEqualTo("Joao Silva");
        verify(authenticationGateway).authenticate("joao@email.com", "password123");
    }

    @Test
    void shouldThrowWhenLoginWithInvalidCredentials() {
        AuthRequest request = new AuthRequest("joao@email.com", "wrongpassword");
        doThrow(new AuthenticationException("Invalid email or password"))
                .when(authenticationGateway).authenticate("joao@email.com", "wrongpassword");

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid email or password");

        verifyNoInteractions(tokenProvider);
    }

    @Test
    void shouldThrowWhenLoginUserIsMissing() {
        AuthRequest request = new AuthRequest("joao@email.com", "password123");
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(tokenProvider.extractSubject("valid-refresh-token")).thenReturn("joao@email.com");
        when(tokenProvider.isTokenValid("valid-refresh-token", "joao@email.com")).thenReturn(true);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(tokenProvider.generateAccessToken("joao@email.com")).thenReturn("new-access-token");
        when(tokenProvider.generateRefreshToken("joao@email.com")).thenReturn("new-refresh-token");

        AuthResponse response = authUseCase.refreshToken(request);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void shouldThrowWhenRefreshWithInvalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(tokenProvider.extractSubject("invalid-token")).thenReturn(null);

        assertThatThrownBy(() -> authUseCase.refreshToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void shouldThrowWhenRefreshWithExpiredToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired-token");
        when(tokenProvider.extractSubject("expired-token")).thenReturn("joao@email.com");
        when(tokenProvider.isTokenValid("expired-token", "joao@email.com")).thenReturn(false);

        assertThatThrownBy(() -> authUseCase.refreshToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid or expired refresh token");
    }

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest("Joao", "joao@email.com", "password123", "1234567890");
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateAccessToken("joao@email.com")).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken("joao@email.com")).thenReturn("refresh-token");

        AuthResponse response = authUseCase.register(request);

        assertThat(response.email()).isEqualTo("joao@email.com");
        assertThat(response.accessToken()).isEqualTo("access-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldPersistEncodedPasswordOnRegister() {
        RegisterRequest request = new RegisterRequest("Joao", "joao@email.com", "password123", "1234567890");
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(tokenProvider.generateAccessToken("joao@email.com")).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken("joao@email.com")).thenReturn("refresh-token");

        authUseCase.register(request);

        var captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encodedPassword123");
    }

    @Test
    void shouldThrowWhenRegisterWithExistingEmail() {
        RegisterRequest request = new RegisterRequest("Joao", "joao@email.com", "password123", "1234567890");
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authUseCase.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already in use");
    }
}
