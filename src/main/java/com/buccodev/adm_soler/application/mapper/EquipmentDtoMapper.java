package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.core.domain.Equipment;

public final class EquipmentDtoMapper {

    private EquipmentDtoMapper() {
    }

    public static Equipment toDomain(EquipmentRequest request) {
        return Equipment.create(request.name(), request.description());
    }

    public static void applyTo(Equipment equipment, EquipmentRequest request) {
        equipment.setName(request.name());
        equipment.setDescription(request.description());
    }

    public static EquipmentResponse toResponse(Equipment equipment) {
        if (equipment == null) {
            return null;
        }
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }
}
