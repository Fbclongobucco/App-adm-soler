package com.buccodev.adm_soler.infra.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.cors")
public class SecurityCorsProperties {

    /** Origens liberadas para o frontend. Sem curinga: a API usa credenciais. */
    private List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:5173");

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
