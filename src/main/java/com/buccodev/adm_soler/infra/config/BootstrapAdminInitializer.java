package com.buccodev.adm_soler.infra.config;

import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro ADMIN se, e somente se, o email configurado ainda nao existe.
 * Nao promove nem repara usuarios existentes: e apenas a semente da base.
 */
@Component
@EnableConfigurationProperties(BootstrapAdminProperties.class)
public class BootstrapAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminInitializer.class);

    private static final String PUBLIC_DEFAULT_PASSWORD = "1234567";

    private final BootstrapAdminProperties properties;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public BootstrapAdminInitializer(BootstrapAdminProperties properties,
                                     UserRepository userRepository,
                                     PasswordHasher passwordHasher) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            return;
        }
        if (isBlank(properties.getEmail()) || isBlank(properties.getPassword())) {
            log.warn("security.bootstrap-admin habilitado sem email/senha: nenhum admin foi criado");
            return;
        }

        String email = properties.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User admin = User.create(properties.getName(), email, properties.getPassword(), null, User.Role.ADMIN);
        admin.applyHashedPassword(passwordHasher.hash(properties.getPassword()));
        userRepository.save(admin);

        log.warn("Admin inicial criado para {}. Troque a senha em PUT /api/v1/auth/me/password", email);
        if (PUBLIC_DEFAULT_PASSWORD.equals(properties.getPassword())) {
            log.warn("ATENCAO: o admin {} esta com a senha default do repositorio, que e publica. "
                    + "Defina BOOTSTRAP_ADMIN_PASSWORD ou troque a senha antes de expor a API", email);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
