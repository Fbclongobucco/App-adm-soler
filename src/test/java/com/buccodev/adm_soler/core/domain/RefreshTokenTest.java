package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void issuedTokenIsUsableBeforeExpiration() {
        RefreshToken token = RefreshToken.issue(USER_ID, "hash", DomainFixture.FEV);

        assertThat(token.isUsable(DomainFixture.JAN)).isTrue();
        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getRevokedAt()).isNull();
    }

    @Test
    void expiredTokenIsNotUsable() {
        RefreshToken token = RefreshToken.issue(USER_ID, "hash", DomainFixture.JAN);

        assertThat(token.isExpired(DomainFixture.FEV)).isTrue();
        assertThat(token.isUsable(DomainFixture.FEV)).isFalse();
    }

    @Test
    void revokeMarksTokenUnusableEvenWithinValidity() {
        RefreshToken token = RefreshToken.issue(USER_ID, "hash", DomainFixture.FEV);

        token.revoke(RefreshToken.RevokedReason.LOGGED_OUT);

        assertThat(token.isRevoked()).isTrue();
        assertThat(token.isUsable(DomainFixture.JAN)).isFalse();
        assertThat(token.getRevokedReason()).isEqualTo(RefreshToken.RevokedReason.LOGGED_OUT);
    }

    @Test
    void onlyARotatedTokenCountsAsReuseEvidence() {
        RefreshToken rotated = RefreshToken.issue(USER_ID, "a", DomainFixture.FEV);
        RefreshToken loggedOut = RefreshToken.issue(USER_ID, "b", DomainFixture.FEV);

        rotated.revoke(RefreshToken.RevokedReason.ROTATED);
        loggedOut.revoke(RefreshToken.RevokedReason.LOGGED_OUT);

        assertThat(rotated.wasRotated()).isTrue();
        assertThat(loggedOut.wasRotated()).isFalse();
    }

    @Test
    void revokeIsIdempotentAndKeepsTheFirstInstant() {
        RefreshToken token = RefreshToken.issue(USER_ID, "hash", DomainFixture.FEV);

        token.revoke(RefreshToken.RevokedReason.ROTATED);
        LocalDateTime first = token.getRevokedAt();
        token.revoke(RefreshToken.RevokedReason.LOGGED_OUT);

        assertThat(token.getRevokedAt()).isEqualTo(first);
        assertThat(token.getRevokedReason()).isEqualTo(RefreshToken.RevokedReason.ROTATED);
    }

    @Test
    void restoreRejectsBlankHash() {
        assertThatThrownBy(() -> RefreshToken.restore(UUID.randomUUID(), USER_ID, "  ",
                DomainFixture.FEV, DomainFixture.JAN, null, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("tokenHash cannot be blank");
    }
}
