package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.RefreshTokenJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpa, UUID> {

    Optional<RefreshTokenJpa> findByTokenHash(String tokenHash);

    List<RefreshTokenJpa> findByUserIdAndRevokedAtIsNull(UUID userId);

    @Modifying
    @Query("delete from RefreshTokenJpa t where t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("delete from RefreshTokenJpa t where t.expiresAt < :instant")
    void deleteByExpiresAtBefore(@Param("instant") LocalDateTime instant);
}
