package com.buccodev.adm_soler.application.dto.restaurant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RestaurantResponseDto(UUID id, String name, String email, String phone, String cnpj,
                                    UUID projectId, UUID addressId, Boolean isBilled,
                                    BigDecimal lunchPrice, BigDecimal dinnerPrice,
                                    BigDecimal additionalValues, Integer days, BigDecimal total,
                                    BigDecimal valuePerEmployee,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
}
