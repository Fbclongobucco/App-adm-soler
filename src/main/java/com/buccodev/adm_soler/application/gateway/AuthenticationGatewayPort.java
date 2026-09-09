package com.buccodev.adm_soler.application.gateway;

/**
 * Porta de saida para verificacao de credenciais.
 * A implementacao lanca {@link com.buccodev.adm_soler.application.exception.AuthenticationException}
 * quando as credenciais nao conferem.
 */
public interface AuthenticationGatewayPort {

    void authenticate(String email, String rawPassword);
}
