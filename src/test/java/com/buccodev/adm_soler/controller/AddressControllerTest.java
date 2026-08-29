package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.address.AddressRequest;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.usecase.AddressUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.AddressController;
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
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddressUseCase addressUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AddressResponse buildResponse() {
        return new AddressResponse(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private AddressRequest buildRequest() {
        return new AddressRequest("Rua A", "100", null, "Centro", "Sao Paulo", "SP", "01000-000", "Brasil");
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        when(addressUseCase.create(any(AddressRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/addresses")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("Rua A"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        mockMvc.perform(post("/api/v1/addresses")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        AddressResponse response = buildResponse();
        when(addressUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/addresses/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Rua A"));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        AddressResponse response = buildResponse();
        when(addressUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/addresses/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(addressUseCase.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(buildResponse()), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/addresses")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        AddressResponse response = buildResponse();
        when(addressUseCase.update(any(UUID.class), any(AddressRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/addresses/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(addressUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/addresses/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/addresses/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/addresses"))
                .andExpect(status().isForbidden());
    }
}
