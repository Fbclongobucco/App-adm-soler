package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.EmployeeJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpa, UUID> {

    Optional<EmployeeJpa> findByEmail(String email);

    boolean existsByEmail(String email);

    List<EmployeeJpa> findByNameContainingIgnoreCase(String name);

    List<EmployeeJpa> findByRole(String role);

    List<EmployeeJpa> findByAddressId(UUID addressId);
}
