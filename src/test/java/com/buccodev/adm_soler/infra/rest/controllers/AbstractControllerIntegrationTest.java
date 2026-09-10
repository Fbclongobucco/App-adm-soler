package com.buccodev.adm_soler.infra.rest.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractControllerIntegrationTest {

    protected static final String ADMIN_EMAIL = "admin@soler.com";
    protected static final String ADMIN_PASSWORD = "admin123";
    protected static final String USER_PASSWORD = "password123";

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    protected String adminAccessToken() throws Exception {
        return login(ADMIN_EMAIL, ADMIN_PASSWORD).get("accessToken").asText();
    }

    protected JsonNode login(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }

    protected JsonNode createUser(String adminToken, String email) throws Exception {
        String body = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Usuario Teste","email":"%s","password":"%s","phone":"11987654321"}
                                """.formatted(email, USER_PASSWORD)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body);
    }

    /** Cria um usuario de papel USER e devolve o access token dele. */
    protected String userAccessToken(String adminToken) throws Exception {
        String email = "user_" + uniqueSuffix() + "@email.com";
        createUser(adminToken, email);
        return login(email, USER_PASSWORD).get("accessToken").asText();
    }

    protected UUID createAddress(String adminToken) throws Exception {
        String body = mockMvc.perform(post("/api/v1/addresses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"street":"Rua das Obras","number":"100","complement":null,
                                 "neighborhood":"Centro","city":"Sao Paulo","state":"SP",
                                 "zipCode":"01000-000","country":"BR"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }

    protected UUID createClient(String adminToken, UUID addressId) throws Exception {
        String body = mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Construtora %s","email":"cli_%s@xyz.com",
                                 "phone":"11987654321","cnpj":"11.222.333/0001-44","addressId":"%s"}
                                """.formatted(uniqueSuffix(), uniqueSuffix(), addressId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }

    protected UUID createProject(String adminToken, UUID clientId) throws Exception {
        String body = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"os":"OS-%s","serviceProvided":"Retrofit de cobertura",
                                 "clientId":"%s","startDate":"2026-01-01T08:00:00",
                                 "endDate":"2026-03-01T18:00:00"}
                                """.formatted(uniqueSuffix(), clientId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }
}
