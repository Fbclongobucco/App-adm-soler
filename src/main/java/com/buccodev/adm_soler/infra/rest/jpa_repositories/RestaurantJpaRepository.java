package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.RestaurantJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantJpa, UUID> {

    List<RestaurantJpa> findByProjectId(UUID projectId);

    List<RestaurantJpa> findByIsBilled(Boolean isBilled);

    Optional<RestaurantJpa> findByEmail(String email);

    Optional<RestaurantJpa> findByCnpj(String cnpj);

    boolean existsByProjectId(UUID projectId);

    List<RestaurantJpa> findByAddressId(UUID addressId);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM RestaurantJpa r WHERE r.project.id = :projectId")
    BigDecimal sumTotalByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM RestaurantJpa r WHERE r.isBilled = :isBilled")
    BigDecimal sumTotalByIsBilled(@Param("isBilled") Boolean isBilled);
}
