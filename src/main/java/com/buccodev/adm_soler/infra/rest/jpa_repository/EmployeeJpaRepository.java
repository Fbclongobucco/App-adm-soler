package com.buccodev.adm_soler.infra.rest.jpa_repository;

import com.buccodev.adm_soler.infra.rest.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, UUID> {
}
