package com.buccodev.adm_soler.application.gateway;

/**
 * Porta de saida para emissao e validacao de tokens de acesso.
 * O subject e sempre o identificador publico do usuario (email).
 */
public interface TokenProviderPort {

    String generateAccessToken(String subject);

    String generateRefreshToken(String subject);

    /**
     * @return o subject do token ou {@code null} quando o token e malformado,
     *         expirado ou possui assinatura invalida.
     */
    String extractSubject(String token);

    boolean isTokenValid(String token, String subject);
}
