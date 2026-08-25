package com.buccodev.adm_soler;

import com.buccodev.adm_soler.infra.rest.entities.UserJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
@EnableJpaRepositories
public class AdmSolerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdmSolerApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userJpaRepository.findByEmail("admin@soler.com").isEmpty()) {
                UserJpa admin = new UserJpa();
                admin.setId(UUID.randomUUID());
                admin.setName("Administrador");
                admin.setEmail("admin@soler.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPhone("0000000000");
                admin.setRole("ADMIN");
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                admin.markAsExisting();
                userJpaRepository.save(admin);
                System.out.println("Admin user created: admin@soler.com / admin123");
            }
        };
    }

}
