package com.buccodev.adm_soler.core.security;

import com.buccodev.adm_soler.core.domain.User;

/**
 * Porta de emissao do access token (JWT). A validacao do token acontece no
 * filtro HTTP, na infraestrutura; aqui so interessa emitir.
 */
public interface AccessTokenProvider {

    String generate(User user);

    /** Tempo de vida do access token, em segundos, para devolver ao cliente. */
    long expiresInSeconds();
}
