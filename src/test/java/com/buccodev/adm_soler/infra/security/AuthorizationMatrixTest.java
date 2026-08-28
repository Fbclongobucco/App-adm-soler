package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.infra.security.config.SecurityConfig;
import com.buccodev.adm_soler.infra.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A matriz de criticidade de {@link SecurityConfig}, exercitada pela cadeia de
 * filtros real e com tokens JWT de verdade.
 */
@SpringBootTest
class AuthorizationMatrixTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;
    private String foreignToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        adminToken = tokenFor(User.Role.ADMIN);
        userToken = tokenFor(User.Role.USER);
        foreignToken = tokenFor(User.Role.FOREIGN);
    }

    private String tokenFor(User.Role role) {
        return tokenProvider.generate(
                User.create(role.name(), role.name().toLowerCase() + "@teste.com", "senha123", null, role));
    }

    // ---------- Nivel 0: publico ----------

    @Test
    void loginIsReachableWithoutAToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ninguem@teste.com\",\"password\":\"senha123\"}"))
                .andExpect(status().isUnauthorized()); // chegou no caso de uso, credencial invalida
    }

    @Test
    void refreshIsReachableWithoutAToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"inexistente\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- Sem credencial valida ----------

    @Test
    void anonymousIsRejectedEverywhereElse() throws Exception {
        mockMvc.perform(get("/api/v1/projects")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void aTamperedTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/projects").header(HttpHeaders.AUTHORIZATION, "Bearer nao.e.um.jwt"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/projects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken + "x"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- Nivel 5: critico, so ADMIN ----------

    @Test
    void onlyAdminManagesUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/users").header(HttpHeaders.AUTHORIZATION, bearer(foreignToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyAdminChangesRoles() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}/role", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyAdminDeletes() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/projects/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/equipments/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer(foreignToken)))
                .andExpect(status().isForbidden());

        // ADMIN passa pela autorizacao: 404 e resposta do caso de uso, nao do filtro.
        mockMvc.perform(delete("/api/v1/projects/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isNotFound());
    }

    // ---------- Nivel 4: dados pessoais ----------

    @Test
    void personalDataIsClosedToExternalProfiles() throws Exception {
        for (String path : new String[]{"/api/v1/clients", "/api/v1/employees", "/api/v1/addresses"}) {
            mockMvc.perform(get(path).header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                    .andExpect(status().isOk());
            mockMvc.perform(get(path).header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                    .andExpect(status().isOk());
            mockMvc.perform(get(path).header(HttpHeaders.AUTHORIZATION, bearer(foreignToken)))
                    .andExpect(status().isForbidden());
        }
    }

    // ---------- Nivel 3: escrita operacional ----------

    @Test
    void externalProfileCannotWriteOperationalData() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .header(HttpHeaders.AUTHORIZATION, bearer(foreignToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/v1/restaurants/{id}/project/{p}", UUID.randomUUID(), UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, bearer(foreignToken)))
                .andExpect(status().isForbidden());
    }

    // ---------- Nivel 2: leitura operacional ----------

    @Test
    void everyAuthenticatedProfileReadsOperationalData() throws Exception {
        for (String token : new String[]{adminToken, userToken, foreignToken}) {
            mockMvc.perform(get("/api/v1/projects").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/api/v1/equipments").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/api/v1/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/api/v1/restaurants").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                    .andExpect(status().isOk());
        }
    }

    // ---------- Fechado por padrao ----------

    @Test
    void anUnmappedRouteIsDeniedEvenForAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/rota-que-nao-existe")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adminToken)))
                .andExpect(status().isForbidden());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
