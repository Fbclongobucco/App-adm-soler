package com.buccodev.adm_soler.integration;

import com.buccodev.adm_soler.infra.rest.jpa_repositories.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    private String createAdminAndGetToken() throws Exception {
        String email = "admin_" + UUID.randomUUID() + "@email.com";
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Admin\",\"email\":\"" + email + "\",\"password\":\"admin123\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        UUID userId = UUID.fromString(response.get("userId").asText());

        userJpaRepository.updateRole(userId, "ADMIN");

        return loginAndGetToken(email, "admin123");
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        var result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("accessToken").asText();
    }

    @Test
    void shouldRegisterAndLoginSuccessfully() throws Exception {
        String email = "joao_" + UUID.randomUUID() + "@email.com";
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Joao Silva\",\"email\":\"" + email + "\",\"password\":\"password123\",\"phone\":\"1234567890\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void shouldReturn401WhenLoginWithWrongPassword() throws Exception {
        String email = "joao2_" + UUID.randomUUID() + "@email.com";
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Joao\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenLoginWithNonExistentEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent_" + UUID.randomUUID() + "@email.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        String email = "joao3_" + UUID.randomUUID() + "@email.com";
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Joao\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        String refreshToken = response.get("refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldReturn401WhenRefreshWithInvalidToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"invalid-token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        String token = createAdminAndGetToken();

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WhenUserTriesAdminEndpoint() throws Exception {
        String email = "useradmin_" + UUID.randomUUID() + "@email.com";
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"User\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        String token = loginAndGetToken(email, "password123");

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenDeletingEquipmentAsUser() throws Exception {
        String email = "userequip_" + UUID.randomUUID() + "@email.com";
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"User\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());

        String token = loginAndGetToken(email, "password123");

        mockMvc.perform(delete("/api/v1/equipments/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer expired.invalid.token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAdminBeAbleToCreateUser() throws Exception {
        String token = createAdminAndGetToken();

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New User\",\"email\":\"new_" + UUID.randomUUID() + "@email.com\",\"password\":\"password123\",\"phone\":\"0987654321\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    void shouldAdminBeAbleToDeleteUser() throws Exception {
        String token = createAdminAndGetToken();

        String email = "delete_" + UUID.randomUUID() + "@email.com";
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"To Delete\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        String userId = response.get("userId").asText();

        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
