package com.buccodev.adm_soler.core.security;

import com.buccodev.adm_soler.core.domain.User;

import java.util.Optional;

public interface TokenService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    /**
     * @return o email do token quando ele e um access token valido; vazio quando
     *         esta expirado, malformado, assinado com outra chave ou e um refresh token.
     */
    Optional<String> extractEmailIfValidAccessToken(String token);

    Optional<String> extractEmailIfValidRefreshToken(String token);
}
