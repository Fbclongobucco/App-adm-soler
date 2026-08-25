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
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    private String registerAndGetToken(String name, String email, String password) throws Exception {
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("accessToken").asText();
    }

    private String registerUserSetRoleAndGetToken(String name, String email, String password, String role) throws Exception {
        var result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        UUID userId = UUID.fromString(response.get("userId").asText());

        userJpaRepository.updateRole(userId, role);

        return loginAndGetToken(email, password);
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
    void authEndpointsShouldBePublic() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@email.com\",\"password\":\"pass\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"newuser@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allEndpointsShouldRequireAuthentication() throws Exception {
        String[] endpoints = {
                "/api/v1/users",
                "/api/v1/employees",
                "/api/v1/clients",
                "/api/v1/projects",
                "/api/v1/addresses",
                "/api/v1/restaurants",
                "/api/v1/equipments",
                "/api/v1/accommodations"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void userShouldBeAbleToReadAllResources() throws Exception {
        String token = registerUserSetRoleAndGetToken("User", "readuser@email.com", "password123", "USER");

        mockMvc.perform(get("/api/v1/employees").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/clients").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/projects").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/addresses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/restaurants").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/equipments").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/accommodations").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userShouldNotBeAbleToCreateResources() throws Exception {
        String token = registerUserSetRoleAndGetToken("User", "writeuser@email.com", "password123", "USER");

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"t@t.com\",\"password\":\"123456\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/equipments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"description\":\"desc\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"street\":\"Rua\",\"city\":\"SP\",\"state\":\"SP\",\"zipCode\":\"00000\",\"country\":\"BR\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAbleToUpdateResources() throws Exception {
        String token = registerUserSetRoleAndGetToken("User", "updateuser@email.com", "password123", "USER");

        mockMvc.perform(put("/api/v1/equipments/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"description\":\"desc\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAbleToDeleteResources() throws Exception {
        String token = registerUserSetRoleAndGetToken("User", "deleteuser@email.com", "password123", "USER");

        mockMvc.perform(delete("/api/v1/equipments/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldBeAbleToAccessAllEndpoints() throws Exception {
        String token = registerUserSetRoleAndGetToken("Admin", "admin@email.com", "admin123", "ADMIN");

        mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/equipments").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/addresses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/projects").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/clients").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/restaurants").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/employees").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/accommodations").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminShouldBeAbleToCreateAndDelete() throws Exception {
        String token = registerUserSetRoleAndGetToken("Admin", "admin2@email.com", "admin123", "ADMIN");

        var result = mockMvc.perform(post("/api/v1/equipments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"description\":\"Dell\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        UUID equipmentId = UUID.fromString(responseBody.get("id").asText());

        mockMvc.perform(delete("/api/v1/equipments/" + equipmentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void foreignUserShouldBeAbleToRead() throws Exception {
        String token = registerUserSetRoleAndGetToken("Foreign", "foreign@email.com", "password123", "FOREIGN");

        mockMvc.perform(get("/api/v1/equipments").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/projects").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void foreignUserShouldNotBeAbleToWrite() throws Exception {
        String token = registerUserSetRoleAndGetToken("Foreign", "foreignwrite@email.com", "password123", "FOREIGN");

        mockMvc.perform(post("/api/v1/equipments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"description\":\"desc\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/equipments/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
