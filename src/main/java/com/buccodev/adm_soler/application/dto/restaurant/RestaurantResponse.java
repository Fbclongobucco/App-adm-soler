package com.buccodev.adm_soler.application.dto.restaurant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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
}
