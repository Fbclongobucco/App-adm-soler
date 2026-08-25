package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.UserUseCase;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.infra.rest.controllers.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserUseCase userUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserResponse buildUserResponse() {
        return new UserResponse(
                UUID.randomUUID(), "Joao Silva", "joao@email.com", "1234567890",
                User.Role.USER, java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateUserWhenAdmin() throws Exception {
        UserRequest request = new UserRequest("Joao", "joao@email.com", "password123", "1234567890");
        when(userUseCase.create(any(UserRequest.class))).thenReturn(buildUserResponse());

        mockMvc.perform(post("/api/v1/users")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        UserRequest request = new UserRequest("Joao", "joao@email.com", "password123", "1234567890");

        mockMvc.perform(post("/api/v1/users")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        UserRequest request = new UserRequest("Joao", "joao@email.com", "password123", "1234567890");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindByIdWhenAdmin() throws Exception {
        UserResponse response = buildUserResponse();
        when(userUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    void shouldFindByIdWhenUser() throws Exception {
        UserResponse response = buildUserResponse();
        when(userUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllWhenAdmin() throws Exception {
        when(userUseCase.findAll()).thenReturn(List.of(buildUserResponse()));

        mockMvc.perform(get("/api/v1/users")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturn403WhenUserTriesFindAll() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateWhenAdmin() throws Exception {
        UserResponse response = buildUserResponse();
        UserRequest request = new UserRequest("Updated", "joao@email.com", "password123", "1234567890");
        when(userUseCase.update(any(UUID.class), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteWhenAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/users/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
