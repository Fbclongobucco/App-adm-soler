package com.buccodev.adm_soler.application.dto.restaurant;

import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantRequest(
        String name,
        String email,
        String phone,
        String cnpj,
        UUID projectId,
        Boolean isBilled,
        BigDecimal lunchPrice,
        BigDecimal dinnerPrice,
        BigDecimal additionalValues,
        Integer days,
        UUID addressId
) {
}
