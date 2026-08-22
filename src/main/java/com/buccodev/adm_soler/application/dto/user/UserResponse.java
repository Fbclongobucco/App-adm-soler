package com.buccodev.adm_soler.application.dto.user;

import com.buccodev.adm_soler.core.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        User.Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
