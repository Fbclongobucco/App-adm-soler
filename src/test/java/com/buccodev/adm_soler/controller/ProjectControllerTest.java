package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.application.usecase.ProjectUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.ProjectController;
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
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectUseCase projectUseCase;

    private ProjectResponse buildResponse() {
        return new ProjectResponse(
                UUID.randomUUID(), "OS-001", "Servico X", UUID.randomUUID(),
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private String buildRequestJson() {
        return "{"
                + "\"os\":\"OS-001\","
                + "\"serviceProvided\":\"Servico X\","
                + "\"clientId\":\"" + UUID.randomUUID() + "\","
                + "\"startDate\":\"" + LocalDateTime.now() + "\","
                + "\"endDate\":\"" + LocalDateTime.now().plusDays(30) + "\""
                + "}";
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        when(projectUseCase.create(any(ProjectRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/projects")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.os").value("OS-001"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        ProjectResponse response = buildResponse();
        when(projectUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/projects/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.os").value("OS-001"));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        ProjectResponse response = buildResponse();
        when(projectUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/projects/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(projectUseCase.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(buildResponse()), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/projects")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        ProjectResponse response = buildResponse();
        when(projectUseCase.update(any(UUID.class), any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/projects/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequestJson()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(projectUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/projects/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isForbidden());
    }
}
