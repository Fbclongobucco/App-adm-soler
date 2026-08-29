package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.application.usecase.ClientUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.ClientController;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientUseCase clientUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AddressResponse buildAddressResponse() {
        return new AddressResponse(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private ClientResponse buildResponse() {
        return new ClientResponse(
                UUID.randomUUID(), "Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", buildAddressResponse(), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private ClientRequest buildRequest() {
        return new ClientRequest("Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", UUID.randomUUID());
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        when(clientUseCase.create(any(ClientRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/clients")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cliente Teste"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        mockMvc.perform(post("/api/v1/clients")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        ClientResponse response = buildResponse();
        when(clientUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente Teste"));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        ClientResponse response = buildResponse();
        when(clientUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(clientUseCase.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(buildResponse()), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/clients")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        ClientResponse response = buildResponse();
        when(clientUseCase.update(any(UUID.class), any(ClientRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/clients/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(clientUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/clients/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/clients/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isForbidden());
    }
}
