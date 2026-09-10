package com.buccodev.adm_soler.infra.rest.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Test
    void projectCarriesTheClientIdInsteadOfANestedObject() throws Exception {
        String token = adminAccessToken();
        UUID addressId = createAddress(token);
        UUID clientId = createClient(token, addressId);
        UUID projectId = createProject(token, clientId);

        mockMvc.perform(get("/api/v1/projects/" + projectId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.client").doesNotExist())
                .andExpect(jsonPath("$.serviceProvided").value("Retrofit de cobertura"));
    }

    @Test
    void unknownClientReturns404() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + adminAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"os":"OS-%s","serviceProvided":"Retrofit","clientId":"%s",
                                 "startDate":"2026-01-01T08:00:00","endDate":"2026-03-01T18:00:00"}
                                """.formatted(uniqueSuffix(), UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void invertedPeriodReturns400FromTheDomain() throws Exception {
        String token = adminAccessToken();
        UUID clientId = createClient(token, createAddress(token));

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"os":"OS-%s","serviceProvided":"Retrofit","clientId":"%s",
                                 "startDate":"2026-03-01T18:00:00","endDate":"2026-01-01T08:00:00"}
                                """.formatted(uniqueSuffix(), clientId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void restaurantExposesTheDerivedTotal() throws Exception {
        String token = adminAccessToken();
        UUID addressId = createAddress(token);
        UUID projectId = createProject(token, createClient(token, addressId));

        mockMvc.perform(post("/api/v1/restaurants")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Restaurante do Ze","email":null,"phone":null,"cnpj":null,
                                 "projectId":"%s","addressId":"%s","isBilled":false,
                                 "lunchPrice":20.00,"dinnerPrice":25.00,"additionalValues":100.00,
                                 "days":10}
                                """.formatted(projectId, addressId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(550.00))
                .andExpect(jsonPath("$.valuePerEmployee").value(0))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()));
    }

    @Test
    void accommodationKeepsItsProjectOnUpdate() throws Exception {
        String token = adminAccessToken();
        UUID addressId = createAddress(token);
        UUID projectId = createProject(token, createClient(token, addressId));

        String body = mockMvc.perform(post("/api/v1/accommodations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"addressId":"%s","projectId":"%s","capacity":4,
                                 "startDate":"2026-01-01T12:00:00","endDate":"2026-02-01T12:00:00"}
                                """.formatted(addressId, projectId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID accommodationId = UUID.fromString(objectMapper.readTree(body).get("id").asText());

        UUID otherProject = createProject(token, createClient(token, addressId));

        mockMvc.perform(put("/api/v1/accommodations/" + accommodationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"addressId":"%s","projectId":"%s","capacity":8,
                                 "startDate":"2026-01-01T12:00:00","endDate":"2026-02-01T12:00:00"}
                                """.formatted(addressId, otherProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(8))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()));
    }
}
