package com.buccodev.adm_soler.infra.security.password;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * O algoritmo de hash das senhas, ao lado de quem o usa.
 *
 * Fica fora do SecurityConfig de proposito: nada na cadeia de filtros depende
 * deste bean - quem o consome e o {@link BCryptPasswordHasher}, adapter da
 * porta de hashing.
 */
@Configuration
public class PasswordEncoderConfig {

    /** BCrypt com custo 12: caro o suficiente para forca bruta, viavel no login. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
