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
 */
public class AuthUseCase {

    /** Mensagem unica para credencial errada: nao revela se o email existe. */
    private static final String INVALID_CREDENTIALS = "Email ou senha invalidos";

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
            throw new BadRequestException("email e password sao obrigatorios");
        }

        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS));

        if (!passwordHasher.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS);
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
            throw new BadRequestException("refreshToken e obrigatorio");
        }

        String fingerprint = refreshTokenCodec.fingerprint(refreshTokenValue);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(fingerprint)
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalido"));

        LocalDateTime now = LocalDateTime.now();
        if (stored.isRevoked()) {
            // Reapresentar um token que ja foi rotacionado significa que duas
            // partes conhecem o mesmo segredo: derruba tudo. Nos outros casos
            // (logout, troca de senha) e so um cliente atrasado, e derrubar as
            // demais sessoes daria a qualquer token velho o poder de deslogar.
            if (stored.wasRotated()) {
                revokeAllSessions(stored.getUserId(), RevokedReason.SECURITY);
                throw new UnauthorizedException("Refresh token ja utilizado; sessoes revogadas");
            }
            throw new UnauthorizedException("Refresh token invalido");
        }
        if (stored.isExpired(now)) {
            stored.revoke(RevokedReason.EXPIRED);
            refreshTokenRepository.save(stored);
            throw new UnauthorizedException("Refresh token expirado");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalido"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + userId)));
    }

    /**
     * Troca da propria senha. Exige a senha atual e revoga as sessoes abertas,
     * para que um token vazado nao sobreviva a troca.
     */
    public void changeOwnPassword(UUID userId, ChangePasswordRequest request) {
        if (request == null || isBlank(request.currentPassword()) || isBlank(request.newPassword())) {
            throw new BadRequestException("currentPassword e newPassword sao obrigatorios");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + userId));

        if (!passwordHasher.matches(request.currentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Senha atual invalida");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new BadRequestException("A nova senha deve ser diferente da atual");
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
