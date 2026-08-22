package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.infra.rest.entities.EquipmentJpa;

public class EquipmentMapper {

    public static EquipmentJpa toJpa(Equipment domain) {
        if (domain == null) return null;
        EquipmentJpa jpa = new EquipmentJpa();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setDescription(domain.getDescription());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static Equipment toDomain(EquipmentJpa jpa) {
        if (jpa == null) return null;
        return Equipment.restore(
                jpa.getId(),
                jpa.getName(),
                jpa.getDescription(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
