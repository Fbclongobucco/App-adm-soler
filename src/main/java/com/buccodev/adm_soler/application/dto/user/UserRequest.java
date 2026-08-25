package com.buccodev.adm_soler.application.dto.user;

import com.buccodev.adm_soler.core.domain.User;

public record UserRequest(
        String name,
        String email,
        String password,
        String phone,
        User.Role role
) {
    /** Perfil aplicado quando o request nao informa um: o menos privilegiado dos internos. */
    public User.Role roleOrDefault() {
        return role != null ? role : User.Role.USER;
    }
}
