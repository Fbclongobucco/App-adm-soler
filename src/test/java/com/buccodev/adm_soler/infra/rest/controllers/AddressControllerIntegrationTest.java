package com.buccodev.adm_soler.infra.rest.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private static final String VALID_BODY = """
            {"street":"Rua das Obras","number":"100","complement":null,"neighborhood":"Centro",
             "city":"Sao Paulo","state":"SP","zipCode":"01000-000","country":"BR"}
            """;

    @Test
    void adminCreatesAndReadsAnAddress() throws Exception {
        String token = adminAccessToken();
        UUID id = createAddress(token);

        mockMvc.perform(get("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Rua das Obras"))
                .andExpect(jsonPath("$.city").value("Sao Paulo"));
    }

    @Test
    void listReturnsAPagedEnvelope() throws Exception {
        String token = adminAccessToken();
        createAddress(token);

        mockMvc.perform(get("/api/v1/addresses?page=0&size=5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void updateReplacesTheAddress() throws Exception {
        String token = adminAccessToken();
        UUID id = createAddress(token);

        mockMvc.perform(put("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"street":"Avenida Nova","number":"200","complement":null,
                                 "neighborhood":"Bairro","city":"Campinas","state":"SP",
                                 "zipCode":"13000-000","country":"BR"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Avenida Nova"))
                .andExpect(jsonPath("$.city").value("Campinas"));
    }

    @Test
    void deleteRemovesTheAddress() throws Exception {
        String token = adminAccessToken();
        UUID id = createAddress(token);

        mockMvc.perform(delete("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void unknownIdReturns404WithProblemDetail() throws Exception {
        mockMvc.perform(get("/api/v1/addresses/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + adminAccessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void blankStreetReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/addresses")
                        .header("Authorization", "Bearer " + adminAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"street":"","number":"1","complement":null,"neighborhood":null,
                                 "city":"Sao Paulo","state":"SP","zipCode":"01000-000","country":"BR"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.street").exists());
    }

    @Test
    void regularUserCanReadButCannotWrite() throws Exception {
        String adminToken = adminAccessToken();
        UUID id = createAddress(adminToken);
        String userToken = userAccessToken(adminToken);

        mockMvc.perform(get("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/addresses")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
