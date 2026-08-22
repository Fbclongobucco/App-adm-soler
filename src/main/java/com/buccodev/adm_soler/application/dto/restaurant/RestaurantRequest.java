package com.buccodev.adm_soler.application.dto.restaurant;

import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;

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
    public Restaurant toDomain(Project project, Address address) {
        return Restaurant.create(
                name,
                email,
                phone,
                project,
                isBilled,
                days,
                address
        );
    }
}
