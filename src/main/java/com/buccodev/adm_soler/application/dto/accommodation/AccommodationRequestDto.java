package com.buccodev.adm_soler.application.dto.accommodation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccommodationRequestDto(@NotNull UUID addressId,
                                      @NotNull UUID projectId,
                                      @Positive Integer capacity,
                                      @NotNull LocalDateTime startDate,
                                      @NotNull LocalDateTime endDate) {}
