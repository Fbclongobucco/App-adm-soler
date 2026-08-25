package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpa, UUID> {

    Optional<UserJpa> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserJpa> findByRole(String role);

    List<UserJpa> findByNameContainingIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query("UPDATE UserJpa u SET u.role = :role WHERE u.id = :id")
    void updateRole(UUID id, String role);
}
