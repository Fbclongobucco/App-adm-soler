package com.buccodev.adm_soler.infra.rest.jpa_repository;

import com.buccodev.adm_soler.infra.rest.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {
}
