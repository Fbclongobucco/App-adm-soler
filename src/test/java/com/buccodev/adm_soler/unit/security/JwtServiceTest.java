package com.buccodev.adm_soler.unit.security;

import com.buccodev.adm_soler.infra.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "dGVzdFNlY3JldEtleUZvclVuaXRUZXN0aW5nUHVycG9zZUFuZEJlTXVzdEJlMjU2Qml0cw==";
    private static final long ACCESS_EXPIRATION = 900000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        var secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, SECRET);

        var accessField = JwtService.class.getDeclaredField("accessTokenExpiration");
        accessField.setAccessible(true);
        accessField.set(jwtService, ACCESS_EXPIRATION);

        var refreshField = JwtService.class.getDeclaredField("refreshTokenExpiration");
        refreshField.setAccessible(true);
        refreshField.set(jwtService, REFRESH_EXPIRATION);
    }

    private UserDetails mockUserDetails() {
        return new User("test@email.com", "password", Collections.emptyList());
    }

    @Test
    void shouldGenerateAccessToken() {
        String token = jwtService.generateAccessToken(mockUserDetails());

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@email.com");
    }

    @Test
    void shouldGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken(mockUserDetails());

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@email.com");
    }

    @Test
    void shouldValidateValidToken() {
        UserDetails userDetails = mockUserDetails();
        String token = jwtService.generateAccessToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        UserDetails userDetails = mockUserDetails();
        UserDetails otherUser = new User("other@email.com", "password", Collections.emptyList());
        String token = jwtService.generateAccessToken(userDetails);

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtService.generateAccessToken(mockUserDetails());

        assertThat(jwtService.extractUsername(token)).isEqualTo("test@email.com");
    }

    @Test
    void shouldNotBeExpired() {
        String token = jwtService.generateAccessToken(mockUserDetails());

        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    void shouldThrowOnMalformedToken() {
        assertThatThrownBy(() -> jwtService.extractUsername("invalid.token.here"))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void shouldThrowOnEmptyToken() {
        assertThatThrownBy(() -> jwtService.extractUsername(""))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldReturnCorrectAccessExpiration() {
        assertThat(jwtService.getAccessTokenExpiration()).isEqualTo(ACCESS_EXPIRATION);
    }

    @Test
    void shouldReturnCorrectRefreshExpiration() {
        assertThat(jwtService.getRefreshTokenExpiration()).isEqualTo(REFRESH_EXPIRATION);
    }
}
