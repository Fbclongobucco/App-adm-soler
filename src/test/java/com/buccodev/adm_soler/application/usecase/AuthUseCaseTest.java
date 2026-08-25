package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.ChangePasswordRequest;
import com.buccodev.adm_soler.application.dto.auth.LoginRequest;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.exception.UnauthorizedException;
import com.buccodev.adm_soler.application.usecase.AuthTestDoubles.FakeAccessTokenProvider;
import com.buccodev.adm_soler.application.usecase.AuthTestDoubles.FakePasswordHasher;
import com.buccodev.adm_soler.application.usecase.AuthTestDoubles.FakeRefreshTokenCodec;
import com.buccodev.adm_soler.application.usecase.AuthTestDoubles.InMemoryRefreshTokenRepository;
import com.buccodev.adm_soler.application.usecase.AuthTestDoubles.InMemoryUserRepository;
import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.core.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthUseCaseTest {

    private static final String EMAIL = "maria@email.com";
    private static final String PASSWORD = "senha123";

    private InMemoryUserRepository users;
    private InMemoryRefreshTokenRepository refreshTokens;
    private FakePasswordHasher hasher;
    private AuthUseCase authUseCase;
    private User maria;

    @BeforeEach
    void setUp() {
        users = new InMemoryUserRepository();
        refreshTokens = new InMemoryRefreshTokenRepository();
        hasher = new FakePasswordHasher();

        maria = User.create("Maria", EMAIL, PASSWORD, null, User.Role.USER);
        maria.applyHashedPassword(hasher.hash(PASSWORD));
        users.save(maria);

        authUseCase = new AuthUseCase(users, refreshTokens, hasher,
                new FakeAccessTokenProvider(), new FakeRefreshTokenCodec(), Duration.ofDays(7));
    }

    @Test
    void loginReturnsBothTokensAndNeverThePassword() {
        AuthResponse response = authUseCase.login(new LoginRequest(EMAIL, PASSWORD));

        assertThat(response.accessToken()).contains(maria.getId().toString());
        assertThat(response.refreshToken()).isEqualTo("refresh-1");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);
        assertThat(response.user().id()).isEqualTo(maria.getId());
    }

    @Test
    void loginStoresOnlyTheFingerprintOfTheRefreshToken() {
        AuthResponse response = authUseCase.login(new LoginRequest(EMAIL, PASSWORD));

        assertThat(refreshTokens.findByTokenHash(response.refreshToken())).isEmpty();
        assertThat(refreshTokens.findByTokenHash("fp::" + response.refreshToken())).isPresent();
    }

    @Test
    void loginNormalizesTheEmailCasing() {
        AuthResponse response = authUseCase.login(new LoginRequest("  MARIA@EMAIL.COM ", PASSWORD));

        assertThat(response.user().email()).isEqualTo(EMAIL);
    }

    @Test
    void wrongPasswordAndUnknownEmailFailTheSameWay() {
        assertThatThrownBy(() -> authUseCase.login(new LoginRequest(EMAIL, "outra-senha")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Email ou senha invalidos");

        assertThatThrownBy(() -> authUseCase.login(new LoginRequest("ninguem@email.com", PASSWORD)))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Email ou senha invalidos");
    }

    @Test
    void loginRejectsMissingCredentials() {
        assertThatThrownBy(() -> authUseCase.login(new LoginRequest(EMAIL, " ")))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void refreshRotatesTheTokenAndRevokesThePreviousOne() {
        String first = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();

        AuthResponse renewed = authUseCase.refresh(first);

        assertThat(renewed.refreshToken()).isNotEqualTo(first);
        assertThat(refreshTokens.findByTokenHash("fp::" + first))
                .get().extracting(RefreshToken::isRevoked).isEqualTo(true);
        assertThat(refreshTokens.findByTokenHash("fp::" + renewed.refreshToken()))
                .get().extracting(RefreshToken::isRevoked).isEqualTo(false);
    }

    @Test
    void reusingARotatedTokenRevokesEverySession() {
        String first = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();
        String second = authUseCase.refresh(first).refreshToken();

        // Reuso de um token ja rotacionado e sinal de vazamento: derruba tudo.
        assertThatThrownBy(() -> authUseCase.refresh(first))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("sessoes revogadas");

        assertThat(refreshTokens.findActiveByUserId(maria.getId())).isEmpty();
        assertThatThrownBy(() -> authUseCase.refresh(second))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void refreshRejectsAnExpiredToken() {
        AuthUseCase expiringUseCase = new AuthUseCase(users, refreshTokens, hasher,
                new FakeAccessTokenProvider(), new FakeRefreshTokenCodec(), Duration.ofSeconds(-1));

        String value = expiringUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();

        assertThatThrownBy(() -> expiringUseCase.refresh(value))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void refreshRejectsAnUnknownToken() {
        assertThatThrownBy(() -> authUseCase.refresh("valor-que-nunca-foi-emitido"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("invalido");
    }

    @Test
    void logoutRevokesOnlyTheSessionPresented() {
        String sessionA = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();
        String sessionB = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();

        authUseCase.logout(sessionA);

        assertThatThrownBy(() -> authUseCase.refresh(sessionA))
                .isInstanceOf(UnauthorizedException.class);
        assertThat(authUseCase.refresh(sessionB)).isNotNull();
    }

    @Test
    void replayingALoggedOutTokenDoesNotKnockOutOtherDevices() {
        String phone = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();
        String desktop = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();

        authUseCase.logout(phone);
        assertThatThrownBy(() -> authUseCase.refresh(phone))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token invalido");

        // Um token velho nao pode servir de alavanca para deslogar os demais.
        assertThat(refreshTokens.findActiveByUserId(maria.getId())).hasSize(1);
        assertThat(authUseCase.refresh(desktop)).isNotNull();
    }

    @Test
    void logoutAllRevokesEverySession() {
        authUseCase.login(new LoginRequest(EMAIL, PASSWORD));
        authUseCase.login(new LoginRequest(EMAIL, PASSWORD));

        authUseCase.logoutAll(maria.getId());

        assertThat(refreshTokens.findActiveByUserId(maria.getId())).isEmpty();
    }

    @Test
    void changingOwnPasswordStoresTheHashAndDropsOpenSessions() {
        String session = authUseCase.login(new LoginRequest(EMAIL, PASSWORD)).refreshToken();

        authUseCase.changeOwnPassword(maria.getId(), new ChangePasswordRequest(PASSWORD, "nova-senha"));

        assertThat(users.findById(maria.getId()).orElseThrow().getPassword())
                .isEqualTo(FakePasswordHasher.PREFIX + "nova-senha");
        assertThat(refreshTokens.findActiveByUserId(maria.getId())).isEmpty();
        assertThatThrownBy(() -> authUseCase.refresh(session))
                .isInstanceOf(UnauthorizedException.class);
        assertThat(authUseCase.login(new LoginRequest(EMAIL, "nova-senha"))).isNotNull();
    }

    @Test
    void changingOwnPasswordRequiresTheCurrentOne() {
        assertThatThrownBy(() -> authUseCase.changeOwnPassword(maria.getId(),
                new ChangePasswordRequest("errada", "nova-senha")))
                .isInstanceOf(UnauthorizedException.class);

        assertThat(users.findById(maria.getId()).orElseThrow().getPassword())
                .isEqualTo(FakePasswordHasher.PREFIX + PASSWORD);
    }

    @Test
    void changingOwnPasswordRejectsAWeakOrRepeatedPassword() {
        assertThatThrownBy(() -> authUseCase.changeOwnPassword(maria.getId(),
                new ChangePasswordRequest(PASSWORD, PASSWORD)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("diferente da atual");

        assertThatThrownBy(() -> authUseCase.changeOwnPassword(maria.getId(),
                new ChangePasswordRequest(PASSWORD, "123")))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void expiredTokensArePurgedByFingerprintlessCleanup() {
        refreshTokens.save(RefreshToken.issue(maria.getId(), "fp::antigo",
                LocalDateTime.now().minusDays(1)));
        authUseCase.login(new LoginRequest(EMAIL, PASSWORD));

        refreshTokens.deleteExpiredBefore(LocalDateTime.now());

        assertThat(refreshTokens.size()).isEqualTo(1);
    }
}
