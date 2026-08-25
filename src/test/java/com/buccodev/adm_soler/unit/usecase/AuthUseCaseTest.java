package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.auth.AuthRequest;
import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.RefreshTokenRequest;
import com.buccodev.adm_soler.application.dto.auth.RegisterRequest;
import com.buccodev.adm_soler.application.exception.AuthenticationException;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.infra.security.CustomUserDetailsService;
import com.buccodev.adm_soler.infra.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User sampleUser;
    private UserDetails sampleUserDetails;

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

        sampleUserDetails = new org.springframework.security.core.userdetails.User(
                "joao@email.com", "encodedPass123", Collections.emptyList());
    }

    @Test
    void shouldLoginSuccessfully() {
        AuthRequest request = new AuthRequest("joao@email.com", "password123");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(customUserDetailsService.loadUserByUsername("joao@email.com")).thenReturn(sampleUserDetails);
        when(jwtService.generateAccessToken(sampleUserDetails)).thenReturn("access-token-123");
        when(jwtService.generateRefreshToken(sampleUserDetails)).thenReturn("refresh-token-123");

        AuthResponse response = authUseCase.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token-123");
        assertThat(response.refreshToken()).isEqualTo("refresh-token-123");
        assertThat(response.email()).isEqualTo("joao@email.com");
        assertThat(response.name()).isEqualTo("Joao Silva");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldThrowWhenLoginWithInvalidCredentials() {
        AuthRequest request = new AuthRequest("joao@email.com", "wrongpassword");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authUseCase.login(request))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    void shouldRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("joao@email.com");
        when(customUserDetailsService.loadUserByUsername("joao@email.com")).thenReturn(sampleUserDetails);
        when(jwtService.isTokenValid("valid-refresh-token", sampleUserDetails)).thenReturn(true);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(sampleUser));
        when(jwtService.generateAccessToken(sampleUserDetails)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(sampleUserDetails)).thenReturn("new-refresh-token");

        AuthResponse response = authUseCase.refreshToken(request);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void shouldThrowWhenRefreshWithInvalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(jwtService.extractUsername("invalid-token")).thenReturn(null);

        assertThatThrownBy(() -> authUseCase.refreshToken(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void shouldThrowWhenRefreshWithExpiredToken() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired-token");
        when(jwtService.extractUsername("expired-token")).thenReturn("joao@email.com");
        when(customUserDetailsService.loadUserByUsername("joao@email.com")).thenReturn(sampleUserDetails);
        when(jwtService.isTokenValid("expired-token", sampleUserDetails)).thenReturn(false);

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
        when(customUserDetailsService.loadUserByUsername("joao@email.com")).thenReturn(sampleUserDetails);
        when(jwtService.generateAccessToken(sampleUserDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(sampleUserDetails)).thenReturn("refresh-token");

        AuthResponse response = authUseCase.register(request);

        assertThat(response.email()).isEqualTo("joao@email.com");
        assertThat(response.accessToken()).isEqualTo("access-token");
        verify(userRepository).save(any(User.class));
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
