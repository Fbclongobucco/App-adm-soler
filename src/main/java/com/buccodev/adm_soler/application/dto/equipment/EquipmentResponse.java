package com.buccodev.adm_soler.application.dto.equipment;

import java.time.LocalDateTime;
import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
