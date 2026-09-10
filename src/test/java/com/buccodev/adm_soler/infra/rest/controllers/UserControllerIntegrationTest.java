package com.buccodev.adm_soler.infra.rest.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Test
    void adminCreatesAUserWithTheUserRole() throws Exception {
        String token = adminAccessToken();

        var created = createUser(token, "novo_" + uniqueSuffix() + "@email.com");

        mockMvc.perform(get("/api/v1/users/" + created.get("id").asText())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void createdUserResponseNeverCarriesThePassword() throws Exception {
        String token = adminAccessToken();

        var created = createUser(token, "sempass_" + uniqueSuffix() + "@email.com");

        org.junit.jupiter.api.Assertions.assertFalse(created.has("password"));
    }

    @Test
    void duplicatedEmailReturns409() throws Exception {
        String token = adminAccessToken();
        String email = "dup_" + uniqueSuffix() + "@email.com";
        createUser(token, email);

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Outro","email":"%s","password":"password123","phone":"11987654321"}
                                """.formatted(email)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shortPasswordReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Curto","email":"curto_%s@email.com","password":"12345",
                                 "phone":"11987654321"}
                                """.formatted(uniqueSuffix())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void malformedPhoneReturns400FromTheDomain() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Telefone","email":"tel_%s@email.com","password":"password123",
                                 "phone":"123"}
                                """.formatted(uniqueSuffix())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void regularUserCannotListUsers() throws Exception {
        String adminToken = adminAccessToken();
        String userToken = userAccessToken(adminToken);

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + adminAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void adminDeletesAUser() throws Exception {
        String token = adminAccessToken();
        var created = createUser(token, "apagar_" + uniqueSuffix() + "@email.com");
        UUID id = UUID.fromString(created.get("id").asText());

        mockMvc.perform(delete("/api/v1/users/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
