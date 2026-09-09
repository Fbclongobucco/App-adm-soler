package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.application.gateway.TokenProviderPort;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final JwtService jwtService;

    public JwtTokenProviderAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateAccessToken(String subject) {
        return jwtService.generateAccessToken(subject);
    }

    @Override
    public String generateRefreshToken(String subject) {
        return jwtService.generateRefreshToken(subject);
    }

    @Override
    public String extractSubject(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token, String subject) {
        try {
            return jwtService.isTokenValid(token, subject);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
