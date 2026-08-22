package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Equipment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository {
    Equipment save(Equipment equipment);
    Optional<Equipment> findById(UUID id);
    List<Equipment> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
