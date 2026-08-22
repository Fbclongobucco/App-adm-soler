package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.ClientJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientJpa, UUID> {

    Optional<ClientJpa> findByEmail(String email);

    Optional<ClientJpa> findByCnpj(String cnpj);

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);

    List<ClientJpa> findByNameContainingIgnoreCase(String name);

    List<ClientJpa> findByAddressId(UUID addressId);
}
