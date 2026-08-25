package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import com.buccodev.adm_soler.infra.rest.entities.RefreshTokenJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.RefreshTokenJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.RefreshTokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpa jpa = RefreshTokenMapper.toJpa(refreshToken);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        RefreshTokenJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return RefreshTokenMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(jpa -> {
            jpa.markAsExisting();
            return RefreshTokenMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<RefreshToken> findActiveByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndRevokedAtIsNull(userId).stream()
                .map(RefreshTokenMapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteExpiredBefore(LocalDateTime instant) {
        jpaRepository.deleteByExpiresAtBefore(instant);
    }
}
