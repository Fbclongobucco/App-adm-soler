package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.core.security.RefreshTokenCodec;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Refresh token = 256 bits de aleatoriedade em base64url.
 *
 * O digest e SHA-256 puro, sem salt, de proposito: precisa ser deterministico
 * para a busca por hash. Isso e seguro aqui porque o segredo nao e uma senha
 * escolhida por humano e sim um valor aleatorio de 256 bits - nao ha dicionario
 * ou rainbow table que o alcance.
 */
@Component
public class Sha256RefreshTokenCodec implements RefreshTokenCodec {

    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    @Override
    public String newValue() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }

    @Override
    public String fingerprint(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel nesta JVM", e);
        }
    }
}
