package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Refresh token emitido para um usuario.
 *
 * O valor em claro nunca e persistido: guardamos apenas o hash, do mesmo jeito
 * que se faz com senha. Assim um dump da tabela nao permite renovar sessoes.
 */
public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private LocalDateTime revokedAt;
    private RevokedReason revokedReason;

    /**
     * Por que o token deixou de valer. Importa para a deteccao de reuso: um
     * token {@link #ROTATED} reapresentado e sinal de vazamento e derruba todas
     * as sessoes; um {@link #LOGGED_OUT} reapresentado e so um cliente atrasado
     * e nao pode servir de alavanca para deslogar os outros dispositivos.
     */
    public enum RevokedReason {
        ROTATED, LOGGED_OUT, EXPIRED, SECURITY
    }

    private RefreshToken(UUID id, UUID userId, String tokenHash, LocalDateTime expiresAt,
                         LocalDateTime createdAt, LocalDateTime revokedAt, RevokedReason revokedReason) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.tokenHash = validateTokenHash(tokenHash);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.revokedAt = revokedAt;
        this.revokedReason = revokedAt != null && revokedReason == null ? RevokedReason.SECURITY : revokedReason;
    }

    public static RefreshToken issue(UUID userId, String tokenHash, LocalDateTime expiresAt) {
        return new RefreshToken(UUID.randomUUID(), userId, tokenHash, expiresAt,
                LocalDateTime.now(), null, null);
    }

    public static RefreshToken restore(UUID id, UUID userId, String tokenHash, LocalDateTime expiresAt,
                                       LocalDateTime createdAt, LocalDateTime revokedAt,
                                       RevokedReason revokedReason) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, createdAt, revokedAt, revokedReason);
    }

    /** Revoga o token. Ja revogado, mantem o instante e o motivo originais. */
    public void revoke(RevokedReason reason) {
        Objects.requireNonNull(reason, "reason is required");
        if (revokedAt == null) {
            this.revokedAt = LocalDateTime.now();
            this.revokedReason = reason;
        }
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return expiresAt.isBefore(Objects.requireNonNull(now, "now is required"));
    }

    public boolean isUsable(LocalDateTime now) {
        return !isRevoked() && !isExpired(now);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public RevokedReason getRevokedReason() {
        return revokedReason;
    }

    /** Reuso de um token ja rotacionado: o unico caso que trata como ataque. */
    public boolean wasRotated() {
        return revokedReason == RevokedReason.ROTATED;
    }

    private String validateTokenHash(String tokenHash) {
        Objects.requireNonNull(tokenHash, "tokenHash is required");
        if (tokenHash.isBlank()) {
            throw new BadRequestException("tokenHash cannot be blank");
        }
        return tokenHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
