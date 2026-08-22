package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.EquipmentJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentJpa, UUID> {

    List<EquipmentJpa> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
