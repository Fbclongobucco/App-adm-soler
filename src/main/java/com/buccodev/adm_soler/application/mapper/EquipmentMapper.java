package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.core.domain.Equipment;

public class EquipmentMapper {

    public static Equipment toDomain(EquipmentRequest request) {
        return Equipment.create(
                request.name(),
                request.description()
        );
    }

    public static EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }
}
