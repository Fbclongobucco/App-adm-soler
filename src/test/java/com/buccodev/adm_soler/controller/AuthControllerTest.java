package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.auth.AuthRequest;
import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.RefreshTokenRequest;
import com.buccodev.adm_soler.application.dto.auth.RegisterRequest;
import com.buccodev.adm_soler.application.exception.AuthenticationException;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.infra.rest.controllers.AuthController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUseCase authUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AuthResponse buildAuthResponse(String email) {
        return new AuthResponse(
                UUID.randomUUID(), "Test User", email, User.Role.USER,
                "access-token-123", "refresh-token-123"
        );
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("test@email.com", "password123");
        when(authUseCase.login(any(AuthRequest.class))).thenReturn(buildAuthResponse("test@email.com"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-123"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    void shouldReturn401WhenLoginFails() throws Exception {
        AuthRequest request = new AuthRequest("test@email.com", "wrongpassword");
        when(authUseCase.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenEmailIsBlank() throws Exception {
        AuthRequest request = new AuthRequest("", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPasswordIsBlank() throws Exception {
        AuthRequest request = new AuthRequest("test@email.com", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(authUseCase.refreshToken(any(RefreshTokenRequest.class)))
                .thenReturn(buildAuthResponse("test@email.com"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldReturn401WhenRefreshTokenInvalid() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(authUseCase.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new AuthenticationException("Invalid refresh token"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("Test", "test@email.com", "password123", "1234567890");
        when(authUseCase.register(any(RegisterRequest.class))).thenReturn(buildAuthResponse("test@email.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldReturn400WhenRegisterEmailExists() throws Exception {
        RegisterRequest request = new RegisterRequest("Test", "existing@email.com", "password123", null);
        when(authUseCase.register(any(RegisterRequest.class)))
                .thenThrow(new BadRequestException("Email already in use"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRegisterPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest("Test", "test@email.com", "12345", null);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
