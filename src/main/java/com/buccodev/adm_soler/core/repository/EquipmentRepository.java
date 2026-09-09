package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository {
    Equipment save(Equipment equipment);
    Optional<Equipment> findById(UUID id);
    PageResult<Equipment> findAll(PageQuery pageQuery);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
