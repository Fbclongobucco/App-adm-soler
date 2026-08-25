package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.security.AccessTokenProvider;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Emissao e verificacao do access token (JWT assinado com HS256).
 *
 * O token carrega id, email e perfil do usuario. Como a autorizacao le o perfil
 * daqui e nao do banco, o TTL e curto: uma mudanca de perfil passa a valer, no
 * pior caso, depois de {@code security.jwt.access-token-ttl}.
 */
@Component
public class JwtTokenProvider implements AccessTokenProvider {

    static final String CLAIM_EMAIL = "email";
    static final String CLAIM_ROLE = "role";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties properties;

    public JwtTokenProvider(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, JwtProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.properties = properties;
    }

    @Override
    public String generate(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(properties.getIssuer())
                .subject(user.getId().toString())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLE, user.getRole().name())
                .issuedAt(now)
                .expiresAt(now.plus(properties.getAccessTokenTtl()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Override
    public long expiresInSeconds() {
        return properties.getAccessTokenTtl().toSeconds();
    }

    /**
     * Valida assinatura, emissor e validade, devolvendo o principal.
     *
     * @throws InvalidTokenException se o token nao for utilizavel
     */
    public AuthenticatedUser parse(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return new AuthenticatedUser(
                    UUID.fromString(jwt.getSubject()),
                    jwt.getClaimAsString(CLAIM_EMAIL),
                    User.Role.valueOf(jwt.getClaimAsString(CLAIM_ROLE))
            );
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            throw new InvalidTokenException(messageFor(e), e);
        }
    }

    private String messageFor(Exception e) {
        boolean expired = e instanceof JwtValidationException validation
                && validation.getErrors().stream()
                        .anyMatch(error -> error.getDescription() != null
                                && error.getDescription().contains("exp"));
        return expired ? "Access token expirado" : "Access token invalido";
    }
}
