package com.buccodev.adm_soler.application.gateway;

/**
 * Porta de saida para hash de senhas. A implementacao concreta (BCrypt, Argon2, ...)
 * vive na camada de infraestrutura.
 */
public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
