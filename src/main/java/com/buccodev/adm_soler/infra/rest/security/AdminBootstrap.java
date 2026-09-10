package com.buccodev.adm_soler.infra.rest.security;

import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final String adminName;
    private final String adminEmail;
    private final String adminPhone;
    private final String adminPassword;

    public AdminBootstrap(UserRepository userRepository, PasswordHasher passwordHasher,
                          @Value("${app.admin.name}") String adminName,
                          @Value("${app.admin.email}") String adminEmail,
                          @Value("${app.admin.phone}") String adminPhone,
                          @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        userRepository.save(User.createAdmin(adminName, adminEmail,
                passwordHasher.hash(adminPassword), adminPhone));
        log.info("Usuario admin criado: {}", adminEmail);
    }
}
