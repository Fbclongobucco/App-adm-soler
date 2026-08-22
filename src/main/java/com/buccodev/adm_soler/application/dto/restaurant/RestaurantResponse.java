package com.buccodev.adm_soler.application.dto.restaurant;

import com.buccodev.adm_soler.core.domain.Restaurant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record RestaurantResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String cnpj,
        UUID projectId,
        Set<UUID> employeeIds,
        Boolean isBilled,
        BigDecimal lunchPrice,
        BigDecimal dinnerPrice,
        BigDecimal total,
        BigDecimal additionalValues,
        BigDecimal valuePerEmployee,
        Integer days,
        UUID addressId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RestaurantResponse fromDomain(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getEmail(),
                restaurant.getPhone(),
                restaurant.getCnpj(),
                restaurant.getProject() != null ? restaurant.getProject().getId() : null,
                restaurant.getEmployees() != null
                        ? restaurant.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                restaurant.getIsBilled(),
                restaurant.getLunchPrice(),
                restaurant.getDinnerPrice(),
                restaurant.getTotal(),
                restaurant.getAdditionalValues(),
                restaurant.getValuePerEmployee(),
                restaurant.getDays(),
                restaurant.getAddress() != null ? restaurant.getAddress().getId() : null,
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }
}
