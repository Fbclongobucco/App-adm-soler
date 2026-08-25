package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.ChangePasswordRequest;
import com.buccodev.adm_soler.application.dto.auth.LoginRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.exception.UnauthorizedException;
import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.core.domain.RefreshToken.RevokedReason;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.AccessTokenProvider;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import com.buccodev.adm_soler.core.security.RefreshTokenCodec;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Autenticacao stateless.
 *
 * O access token e um JWT curto que o servidor nao guarda: toda requisicao se
 * valida sozinha pela assinatura. O refresh token e um valor opaco, longo e
 * persistido em hash, para que uma sessao possa ser revogada - o unico estado
 * de sessao que existe.
 *
 * As mensagens de erro vivem nas fabricas das proprias excecoes: o caso de uso
 * diz o que aconteceu, nao como se escreve.
 */
public class AuthUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenCodec refreshTokenCodec;
    private final Duration refreshTokenTtl;

    public AuthUseCase(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordHasher passwordHasher,
                       AccessTokenProvider accessTokenProvider,
                       RefreshTokenCodec refreshTokenCodec,
                       Duration refreshTokenTtl) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.refreshTokenRepository = Objects.requireNonNull(refreshTokenRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.accessTokenProvider = Objects.requireNonNull(accessTokenProvider);
        this.refreshTokenCodec = Objects.requireNonNull(refreshTokenCodec);
        this.refreshTokenTtl = Objects.requireNonNull(refreshTokenTtl);
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw BadRequestException.missingLoginCredentials();
        }

        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(UnauthorizedException::invalidCredentials);

        if (!passwordHasher.matches(request.password(), user.getPassword())) {
            throw UnauthorizedException.invalidCredentials();
        }

        return issueTokens(user);
    }

    /**
     * Renova a sessao com rotacao: o refresh token apresentado e revogado e um
     * novo par e emitido. Um token ja usado, revogado ou expirado derruba todas
     * as sessoes do usuario - reuso e sinal de vazamento.
     */
    public AuthResponse refresh(String refreshTokenValue) {
        if (isBlank(refreshTokenValue)) {
            throw BadRequestException.missingRefreshToken();
        }

        String fingerprint = refreshTokenCodec.fingerprint(refreshTokenValue);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(fingerprint)
                .orElseThrow(UnauthorizedException::invalidRefreshToken);

        LocalDateTime now = LocalDateTime.now();
        if (stored.isRevoked()) {
            // Reapresentar um token que ja foi rotacionado significa que duas
            // partes conhecem o mesmo segredo: derruba tudo. Nos outros casos
            // (logout, troca de senha) e so um cliente atrasado, e derrubar as
            // demais sessoes daria a qualquer token velho o poder de deslogar.
            if (stored.wasRotated()) {
                revokeAllSessions(stored.getUserId(), RevokedReason.SECURITY);
                throw UnauthorizedException.refreshTokenReused();
            }
            throw UnauthorizedException.invalidRefreshToken();
        }
        if (stored.isExpired(now)) {
            stored.revoke(RevokedReason.EXPIRED);
            refreshTokenRepository.save(stored);
            throw UnauthorizedException.expiredRefreshToken();
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(UnauthorizedException::invalidRefreshToken);

        stored.revoke(RevokedReason.ROTATED);
        refreshTokenRepository.save(stored);

        return issueTokens(user);
    }

    /** Encerra a sessao correspondente ao refresh token apresentado. */
    public void logout(String refreshTokenValue) {
        if (isBlank(refreshTokenValue)) {
            return;
        }
        refreshTokenRepository.findByTokenHash(refreshTokenCodec.fingerprint(refreshTokenValue))
                .ifPresent(token -> {
                    token.revoke(RevokedReason.LOGGED_OUT);
                    refreshTokenRepository.save(token);
                });
    }

    /** Encerra todas as sessoes do usuario (troca de senha, perda de device). */
    public void logoutAll(UUID userId) {
        revokeAllSessions(Objects.requireNonNull(userId, "userId is required"), RevokedReason.LOGGED_OUT);
    }

    public UserResponse me(UUID userId) {
        return UserResponse.fromDomain(userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(userId)));
    }

    /**
     * Troca da propria senha. Exige a senha atual e revoga as sessoes abertas,
     * para que um token vazado nao sobreviva a troca.
     */
    public void changeOwnPassword(UUID userId, ChangePasswordRequest request) {
        if (request == null || isBlank(request.currentPassword()) || isBlank(request.newPassword())) {
            throw BadRequestException.missingPasswordChangeFields();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(userId));

        if (!passwordHasher.matches(request.currentPassword(), user.getPassword())) {
            throw UnauthorizedException.invalidCurrentPassword();
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw BadRequestException.newPasswordMustDiffer();
        }

        User.requireStrongPassword(request.newPassword());
        user.applyHashedPassword(passwordHasher.hash(request.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        revokeAllSessions(userId, RevokedReason.SECURITY);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = accessTokenProvider.generate(user);
        String refreshValue = refreshTokenCodec.newValue();

        refreshTokenRepository.save(RefreshToken.issue(
                user.getId(),
                refreshTokenCodec.fingerprint(refreshValue),
                LocalDateTime.now().plus(refreshTokenTtl)
        ));

        return AuthResponse.of(
                accessToken,
                refreshValue,
                accessTokenProvider.expiresInSeconds(),
                UserResponse.fromDomain(user)
        );
    }

    private void revokeAllSessions(UUID userId, RevokedReason reason) {
        refreshTokenRepository.findActiveByUserId(userId).forEach(token -> {
            token.revoke(reason);
            refreshTokenRepository.save(token);
        });
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
