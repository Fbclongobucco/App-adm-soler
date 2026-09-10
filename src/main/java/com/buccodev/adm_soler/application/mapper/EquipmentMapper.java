package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequestDto;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponseDto;
import com.buccodev.adm_soler.core.domain.Equipment;

public final class EquipmentMapper {

    private EquipmentMapper() {
    }

    public static Equipment toDomain(EquipmentRequestDto dto) {
        return Equipment.create(dto.name(), dto.description());
    }

    public static EquipmentResponseDto toResponseDto(Equipment equipment) {
        return new EquipmentResponseDto(equipment.getId(), equipment.getName(),
                equipment.getDescription(), equipment.getCreatedAt(), equipment.getUpdatedAt());
    }
}
