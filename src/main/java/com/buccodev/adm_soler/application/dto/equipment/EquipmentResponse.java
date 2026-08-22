package com.buccodev.adm_soler.application.dto.equipment;

import com.buccodev.adm_soler.core.domain.Equipment;

import java.time.LocalDateTime;
import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EquipmentResponse fromDomain(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }
}
