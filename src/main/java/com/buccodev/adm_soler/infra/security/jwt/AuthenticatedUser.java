package com.buccodev.adm_soler.infra.security.jwt;

import com.buccodev.adm_soler.core.domain.User;

import java.util.UUID;

/**
 * Principal reconstruido a partir do access token. Nao ha consulta ao banco
 * para autenticar: tudo que a requisicao precisa saber esta assinado no JWT.
 */
public record AuthenticatedUser(
        UUID id,
        String email,
        User.Role role
) {
}
