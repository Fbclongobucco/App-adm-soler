package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.ProjectJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectJpa, UUID> {

    List<ProjectJpa> findByClientId(UUID clientId);

    List<ProjectJpa> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByClientId(UUID clientId);

    long countByClientId(UUID clientId);

    List<ProjectJpa> findByServiceProvidedContainingIgnoreCase(String serviceProvided);

    Optional<ProjectJpa> findByOs(String os);
}
