package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.EquipmentUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.EquipmentController;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipmentUseCase equipmentUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EquipmentResponse buildResponse() {
        return new EquipmentResponse(
                UUID.randomUUID(), "Notebook Dell", "Notebook para dev",
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        EquipmentRequest request = new EquipmentRequest("Notebook Dell", "Notebook");
        when(equipmentUseCase.create(any(EquipmentRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/equipments")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Notebook Dell"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        EquipmentRequest request = new EquipmentRequest("Notebook", "desc");

        mockMvc.perform(post("/api/v1/equipments")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        EquipmentResponse response = buildResponse();
        when(equipmentUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/equipments/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Dell"));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        EquipmentResponse response = buildResponse();
        when(equipmentUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/equipments/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(equipmentUseCase.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/equipments")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        EquipmentResponse response = buildResponse();
        EquipmentRequest request = new EquipmentRequest("Notebook HP", "Updated");
        when(equipmentUseCase.update(any(UUID.class), any(EquipmentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/equipments/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(equipmentUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/equipments/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/equipments/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/equipments"))
                .andExpect(status().isForbidden());
    }
}
