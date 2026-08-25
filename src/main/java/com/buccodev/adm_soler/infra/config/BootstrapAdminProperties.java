package com.buccodev.adm_soler.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Admin inicial. Sem ele a base sobe vazia e ninguem consegue criar o primeiro
 * usuario, ja que /api/v1/users e restrito a ADMIN.
 */
@ConfigurationProperties(prefix = "security.bootstrap-admin")
public class BootstrapAdminProperties {

    private boolean enabled = true;
    private String name = "Administrador";
    private String email;
    private String password;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
