package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findActiveByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    void deleteExpiredBefore(LocalDateTime instant);
}
