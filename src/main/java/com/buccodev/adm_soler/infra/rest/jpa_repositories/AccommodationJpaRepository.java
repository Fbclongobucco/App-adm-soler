package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.AccommodationJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AccommodationJpaRepository extends JpaRepository<AccommodationJpa, UUID> {

    List<AccommodationJpa> findByProjectId(UUID projectId);

    List<AccommodationJpa> findByAddressId(UUID addressId);

    List<AccommodationJpa> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    List<AccommodationJpa> findByProjectIdAndStartDateBetween(UUID projectId, LocalDateTime start, LocalDateTime end);

    boolean existsByProjectId(UUID projectId);

    long countByProjectId(UUID projectId);
}
