package com.buccodev.adm_soler.infra.rest.jpa_repository;

import com.buccodev.adm_soler.infra.rest.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, UUID> {
}
