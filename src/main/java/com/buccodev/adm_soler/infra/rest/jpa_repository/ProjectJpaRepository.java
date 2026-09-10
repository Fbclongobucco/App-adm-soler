package com.buccodev.adm_soler.infra.rest.jpa_repository;

import com.buccodev.adm_soler.infra.rest.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {
}
