package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.infra.rest.entities.RefreshTokenJpa;

public class RefreshTokenMapper {

    public static RefreshTokenJpa toJpa(RefreshToken domain) {
        if (domain == null) return null;
        RefreshTokenJpa jpa = new RefreshTokenJpa();
        jpa.setId(domain.getId());
        jpa.setUserId(domain.getUserId());
        jpa.setTokenHash(domain.getTokenHash());
        jpa.setExpiresAt(domain.getExpiresAt());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setRevokedAt(domain.getRevokedAt());
        jpa.setRevokedReason(domain.getRevokedReason() != null ? domain.getRevokedReason().name() : null);
        return jpa;
    }

    public static RefreshToken toDomain(RefreshTokenJpa jpa) {
        if (jpa == null) return null;
        return RefreshToken.restore(
                jpa.getId(),
                jpa.getUserId(),
                jpa.getTokenHash(),
                jpa.getExpiresAt(),
                jpa.getCreatedAt(),
                jpa.getRevokedAt(),
                jpa.getRevokedReason() != null
                        ? RefreshToken.RevokedReason.valueOf(jpa.getRevokedReason())
                        : null
        );
    }
}
