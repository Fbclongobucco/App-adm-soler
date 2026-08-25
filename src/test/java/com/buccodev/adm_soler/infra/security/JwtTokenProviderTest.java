package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.core.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "segredo-de-teste-com-mais-de-32-caracteres";
    private static final String OTHER_SECRET = "outro-segredo-tambem-com-32-caracteres-ok";

    private final JwtCodecConfig codecs = new JwtCodecConfig();

    private JwtTokenProvider providerWith(String secret, String issuer, Duration ttl) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setIssuer(issuer);
        properties.setAccessTokenTtl(ttl);

        var key = codecs.jwtSigningKey(properties);
        return new JwtTokenProvider(codecs.jwtEncoder(key), codecs.jwtDecoder(key, properties), properties);
    }

    private User admin() {
        return User.create("Admin", "admin@teste.com", "senha123", null, User.Role.ADMIN);
    }

    @Test
    void tokenCarriesIdentityAndRole() {
        JwtTokenProvider provider = providerWith(SECRET, "adm-soler", Duration.ofMinutes(15));
        User user = admin();

        AuthenticatedUser principal = provider.parse(provider.generate(user));

        assertThat(principal.id()).isEqualTo(user.getId());
        assertThat(principal.email()).isEqualTo("admin@teste.com");
        assertThat(principal.role()).isEqualTo(User.Role.ADMIN);
        assertThat(provider.expiresInSeconds()).isEqualTo(900);
    }

    @Test
    void aTokenSignedWithAnotherKeyIsRejected() {
        String foreignToken = providerWith(OTHER_SECRET, "adm-soler", Duration.ofMinutes(15))
                .generate(admin());
        JwtTokenProvider provider = providerWith(SECRET, "adm-soler", Duration.ofMinutes(15));

        assertThatThrownBy(() -> provider.parse(foreignToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Access token invalido");
    }

    @Test
    void aTokenFromAnotherIssuerIsRejected() {
        String otherIssuerToken = providerWith(SECRET, "outra-aplicacao", Duration.ofMinutes(15))
                .generate(admin());
        JwtTokenProvider provider = providerWith(SECRET, "adm-soler", Duration.ofMinutes(15));

        assertThatThrownBy(() -> provider.parse(otherIssuerToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void anExpiredTokenIsReportedAsExpired() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setIssuer("adm-soler");
        var key = codecs.jwtSigningKey(properties);
        JwtEncoder encoder = codecs.jwtEncoder(key);
        JwtTokenProvider provider = new JwtTokenProvider(encoder, codecs.jwtDecoder(key, properties), properties);

        // Assinatura valida, prazo vencido. O generate() sempre emite com data
        // futura, entao o token vencido e montado direto pelo encoder.
        Instant issued = Instant.now().minusSeconds(3600);
        String expiredToken = encoder.encode(JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        JwtClaimsSet.builder()
                                .id(UUID.randomUUID().toString())
                                .issuer("adm-soler")
                                .subject(UUID.randomUUID().toString())
                                .claim(JwtTokenProvider.CLAIM_EMAIL, "admin@teste.com")
                                .claim(JwtTokenProvider.CLAIM_ROLE, "ADMIN")
                                .issuedAt(issued)
                                .expiresAt(issued.plusSeconds(60))
                                .build()))
                .getTokenValue();

        assertThatThrownBy(() -> provider.parse(expiredToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Access token expirado");
    }

    @Test
    void aTamperedTokenIsRejected() {
        JwtTokenProvider provider = providerWith(SECRET, "adm-soler", Duration.ofMinutes(15));
        String token = provider.generate(admin());

        assertThatThrownBy(() -> provider.parse(token.substring(0, token.length() - 4) + "AAAA"))
                .isInstanceOf(InvalidTokenException.class);
        assertThatThrownBy(() -> provider.parse("lixo"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void aShortSecretIsRefusedAtConfigurationTime() {
        JwtProperties properties = new JwtProperties();

        assertThatThrownBy(() -> properties.setSecret("curto"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ao menos 32 caracteres");
    }
}
