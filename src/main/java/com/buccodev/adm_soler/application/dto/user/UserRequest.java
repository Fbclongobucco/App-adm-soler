package com.buccodev.adm_soler.application.dto.user;

import com.buccodev.adm_soler.core.domain.User;

public record UserRequest(
        String name,
        String email,
        String password,
        String phone
) {
    public User toDomain() {
        return User.create(
                name,
                email,
                password,
                phone
        );
    }

    public User toDomain(java.util.UUID id) {
        return User.restore(
                id,
                name,
                email,
                password,
                phone,
                User.Role.USER,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }
}
