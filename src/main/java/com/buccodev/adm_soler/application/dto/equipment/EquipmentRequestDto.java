package com.buccodev.adm_soler.application.dto.equipment;

import jakarta.validation.constraints.NotBlank;

public record EquipmentRequestDto(@NotBlank String name, String description) {}
