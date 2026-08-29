package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.AccommodationController;
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
class AccommodationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccommodationUseCase accommodationUseCase;

    private AccommodationResponse buildResponse() {
        return new AccommodationResponse(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private String buildRequestJson() {
        return "{"
                + "\"addressId\":\"" + UUID.randomUUID() + "\","
                + "\"projectId\":\"" + UUID.randomUUID() + "\","
                + "\"capacity\":10,"
                + "\"startDate\":\"" + LocalDateTime.now() + "\","
                + "\"endDate\":\"" + LocalDateTime.now().plusDays(10) + "\""
                + "}";
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        when(accommodationUseCase.create(any(AccommodationRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/accommodations")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        mockMvc.perform(post("/api/v1/accommodations")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        AccommodationResponse response = buildResponse();
        when(accommodationUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/accommodations/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        AccommodationResponse response = buildResponse();
        when(accommodationUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/accommodations/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(accommodationUseCase.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(buildResponse()), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/accommodations")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        AccommodationResponse response = buildResponse();
        when(accommodationUseCase.update(any(UUID.class), any(AccommodationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/accommodations/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(accommodationUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/accommodations/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/accommodations/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/accommodations"))
                .andExpect(status().isForbidden());
    }
}
