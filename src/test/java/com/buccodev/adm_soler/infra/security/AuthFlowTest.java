package com.buccodev.adm_soler.infra.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo de login ponta a ponta, com o admin semeado pelo perfil h2.
 */
@SpringBootTest
class AuthFlowTest {

    private static final String ADMIN_EMAIL = "email@soler.com.br";
    private static final String ADMIN_PASSWORD = "1234567";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private JsonNode login(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }

    @Test
    void loginReturnsAUsableAccessTokenAndHidesThePassword() throws Exception {
        JsonNode tokens = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        assertThat(tokens.get("tokenType").asString()).isEqualTo("Bearer");
        assertThat(tokens.get("expiresIn").asLong()).isPositive();
        assertThat(tokens.has("password")).isFalse();
        assertThat(tokens.get("user").has("password")).isFalse();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.get("accessToken").asString()))
                .andExpect(status().isOk());
    }

    @Test
    void refreshRotatesAndTheOldTokenStopsWorking() throws Exception {
        String first = login(ADMIN_EMAIL, ADMIN_PASSWORD).get("refreshToken").asString();

        String renewedBody = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + first + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode renewed = objectMapper.readTree(renewedBody);

        assertThat(renewed.get("refreshToken").asString()).isNotEqualTo(first);

        // Reuso do token rotacionado: negado e todas as sessoes caem.
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + first + "\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + renewed.get("refreshToken").asString() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutInvalidatesOnlyTheSessionPresented() throws Exception {
        String phone = login(ADMIN_EMAIL, ADMIN_PASSWORD).get("refreshToken").asString();
        JsonNode desktop = login(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + desktop.get("accessToken").asString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + phone + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + phone + "\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + desktop.get("refreshToken").asString() + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void wrongCredentialsDoNotRevealWhetherTheEmailExists() throws Exception {
        String existing = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ADMIN_EMAIL + "\",\"password\":\"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        String unknown = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ninguem@soler.com.br\",\"password\":\"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(existing).get("message").asString())
                .isEqualTo(objectMapper.readTree(unknown).get("message").asString());
    }

    @Test
    void usersEndpointNeverReturnsPasswordHashes() throws Exception {
        String token = login(ADMIN_EMAIL, ADMIN_PASSWORD).get("accessToken").asString();

        String body = mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("password").doesNotContain("$2a$");
    }

    @Test
    void malformedBodyIsARequestErrorNotAServerError() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":"))
                .andExpect(status().isBadRequest());
    }
}
