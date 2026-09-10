package com.buccodev.adm_soler.application.dto.restaurant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record RestaurantRequestDto(@NotBlank String name,
                                   @Email String email,
                                   String phone,
                                   String cnpj,
                                   @NotNull UUID projectId,
                                   @NotNull UUID addressId,
                                   Boolean isBilled,
                                   @PositiveOrZero BigDecimal lunchPrice,
                                   @PositiveOrZero BigDecimal dinnerPrice,
                                   @PositiveOrZero BigDecimal additionalValues,
                                   @Positive Integer days) {}
