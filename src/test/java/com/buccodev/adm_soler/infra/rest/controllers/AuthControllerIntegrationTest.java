package com.buccodev.adm_soler.infra.rest.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Test
    void loginReturnsBothTokens() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"errada"}
                                """.formatted(ADMIN_EMAIL)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithUnknownEmailReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ninguem_%s@email.com","password":"password123"}
                                """.formatted(uniqueSuffix())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithBlankEmailReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void refreshExchangesTheRefreshTokenForANewAccessToken() throws Exception {
        String refreshToken = login(ADMIN_EMAIL, ADMIN_PASSWORD).get("refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"%s\"}".formatted(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void refreshRejectsAnAccessTokenBecauseOfTheTypeClaim() throws Exception {
        String accessToken = adminAccessToken();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"%s\"}".formatted(accessToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshRejectsAGarbageToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"nao.e.um.token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aRefreshTokenIsNotAcceptedAsAnAccessCredential() throws Exception {
        String refreshToken = login(ADMIN_EMAIL, ADMIN_PASSWORD).get("refreshToken").asText();

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointRejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/addresses"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointRejectsGarbageToken() throws Exception {
        mockMvc.perform(get("/api/v1/addresses")
                        .header("Authorization", "Bearer invalido.invalido.invalido"))
                .andExpect(status().isForbidden());
    }
}
