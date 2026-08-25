package com.buccodev.adm_soler.core.security;

/**
 * Porta de hashing de senha. A implementacao (BCrypt) vive na infraestrutura,
 * para que os casos de uso nao dependam de nenhum framework de seguranca.
 */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
