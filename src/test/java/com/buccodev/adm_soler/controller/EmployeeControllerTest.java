package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.application.usecase.EmployeeUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.EmployeeController;
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
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeUseCase employeeUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AddressResponse buildAddressResponse() {
        return new AddressResponse(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private EmployeeResponse buildResponse() {
        return new EmployeeResponse(
                UUID.randomUUID(), "Funcionario Teste", "func@email.com", "1234567890",
                buildAddressResponse(), "Motorista", LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private EmployeeRequest buildRequest() {
        return new EmployeeRequest("Funcionario Teste", "func@email.com", "1234567890",
                UUID.randomUUID(), "Motorista");
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        when(employeeUseCase.create(any(EmployeeRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/employees")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Funcionario Teste"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        mockMvc.perform(post("/api/v1/employees")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        EmployeeResponse response = buildResponse();
        when(employeeUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Funcionario Teste"));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        EmployeeResponse response = buildResponse();
        when(employeeUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(employeeUseCase.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(buildResponse()), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/employees")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        EmployeeResponse response = buildResponse();
        when(employeeUseCase.update(any(UUID.class), any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/employees/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(employeeUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/employees/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/employees/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isForbidden());
    }
}
