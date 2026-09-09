package com.buccodev.adm_soler.infra.config;

import com.buccodev.adm_soler.application.gateway.PasswordEncoderPort;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Cria o usuario administrador inicial. Passa pelas portas do dominio
 * ({@link UserRepository} / {@link PasswordEncoderPort}) em vez de tocar o JPA direto.
 */
@Configuration
public class AdminBootstrap {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private static final String ADMIN_EMAIL = "admin@soler.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        return args -> {
            if (userRepository.existsByEmail(ADMIN_EMAIL)) {
                return;
            }
            var now = LocalDateTime.now();
            User admin = User.restore(
                    UUID.randomUUID(),
                    "Administrador",
                    ADMIN_EMAIL,
                    passwordEncoder.encode(ADMIN_PASSWORD),
                    "0000000000",
                    User.Role.ADMIN,
                    now,
                    now
            );
            userRepository.save(admin);
            log.info("Usuario admin criado: {}", ADMIN_EMAIL);
        };
    }
}
