package com.buccodev.adm_soler.application.dto.equipment;

import com.buccodev.adm_soler.core.domain.Equipment;

public record EquipmentRequest(
        String name,
        String description
) {
    public Equipment toDomain() {
        return Equipment.create(
                name,
                description
        );
    }
}
