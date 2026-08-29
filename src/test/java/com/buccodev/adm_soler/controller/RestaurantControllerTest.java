package com.buccodev.adm_soler.controller;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.application.usecase.RestaurantUseCase;
import com.buccodev.adm_soler.infra.rest.controllers.RestaurantController;
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

import java.math.BigDecimal;
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
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantUseCase restaurantUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestaurantResponse buildResponse() {
        return new RestaurantResponse(
                UUID.randomUUID(), "Restaurante Teste", "rest@email.com", "1234567890",
                "12.345.678/0001-99", UUID.randomUUID(), Collections.emptySet(), true,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                5, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private RestaurantRequest buildRequest() {
        return new RestaurantRequest("Restaurante Teste", "rest@email.com", "1234567890",
                "12.345.678/0001-99", UUID.randomUUID(), true, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ZERO, 5, UUID.randomUUID());
    }

    @Test
    void shouldCreateWhenAdmin() throws Exception {
        when(restaurantUseCase.create(any(RestaurantRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Restaurante Teste"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreate() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindById() throws Exception {
        RestaurantResponse response = buildResponse();
        when(restaurantUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/restaurants/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Restaurante Teste"));
    }

    @Test
    void shouldFindByIdWhenForeign() throws Exception {
        RestaurantResponse response = buildResponse();
        when(restaurantUseCase.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/restaurants/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("FOREIGN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAll() throws Exception {
        when(restaurantUseCase.findAll(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(List.of(buildResponse()), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/restaurants")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdate() throws Exception {
        RestaurantResponse response = buildResponse();
        when(restaurantUseCase.update(any(UUID.class), any(RestaurantRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/restaurants/" + response.id())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(restaurantUseCase).delete(id);

        mockMvc.perform(delete("/api/v1/restaurants/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenUserTriesToDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/restaurants/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.user("test@email.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isForbidden());
    }
}
