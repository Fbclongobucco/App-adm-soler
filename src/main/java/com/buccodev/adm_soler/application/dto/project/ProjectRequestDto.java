package com.buccodev.adm_soler.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectRequestDto(@NotBlank String os,
                                @NotBlank String serviceProvided,
                                @NotNull UUID clientId,
                                @NotNull LocalDateTime startDate,
                                @NotNull LocalDateTime endDate) {}
