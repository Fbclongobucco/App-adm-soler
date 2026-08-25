package com.buccodev.adm_soler.application.dto.auth;

import com.buccodev.adm_soler.core.domain.User;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String name,
        String email,
        User.Role role,
        String accessToken,
        String refreshToken
) {}
