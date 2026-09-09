package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.application.exception.AuthenticationException;
import com.buccodev.adm_soler.application.gateway.AuthenticationGatewayPort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SpringAuthenticationGatewayAdapter implements AuthenticationGatewayPort {

    private final AuthenticationManager authenticationManager;

    public SpringAuthenticationGatewayAdapter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void authenticate(String email, String rawPassword) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, rawPassword));
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }
}
