package com.buccodev.adm_soler.infra.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Chave e codecs do JWT.
 *
 * Assinatura simetrica (HS256): esta aplicacao e ao mesmo tempo quem emite e
 * quem valida o token, entao nao ha ganho em separar chave publica e privada.
 */
@Configuration
public class JwtCodecConfig {

    @Bean
    public SecretKey jwtSigningKey(JwtProperties properties) {
        if (properties.getSecret() == null) {
            throw new IllegalStateException(
                    "security.jwt.secret nao configurado: defina a variavel de ambiente JWT_SECRET");
        }
        return new SecretKeySpec(properties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSigningKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSigningKey, JwtProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(jwtSigningKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Assinatura valida nao basta: exigimos emissor conhecido, prazo dentro
        // da validade e um subject presente.
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                new JwtIssuerValidator(properties.getIssuer()),
                new JwtClaimValidator<String>(JwtClaimNames.SUB, Objects::nonNull)
        ));

        return decoder;
    }
}
