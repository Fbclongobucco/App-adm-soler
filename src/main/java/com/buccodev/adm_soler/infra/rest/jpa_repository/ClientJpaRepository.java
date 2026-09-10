package com.buccodev.adm_soler.infra.rest.jpa_repository;

import com.buccodev.adm_soler.infra.rest.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {
}
