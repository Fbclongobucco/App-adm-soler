package com.buccodev.adm_soler.infra.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private static final int MIN_SECRET_LENGTH = 32;

    private String secret;
    private String issuer = "adm-soler";
    private Duration accessTokenTtl = Duration.ofMinutes(15);
    private Duration refreshTokenTtl = Duration.ofDays(7);

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "security.jwt.secret deve ter ao menos " + MIN_SECRET_LENGTH + " caracteres (HS256)");
        }
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }

    public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }
}
