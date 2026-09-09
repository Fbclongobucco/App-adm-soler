package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.RegisterRequest;
import com.buccodev.adm_soler.core.domain.User;

public final class AuthDtoMapper {

    private AuthDtoMapper() {
    }

    /**
     * Monta o dominio com a senha em claro para que as invariantes de {@link User}
     * a validem. O caso de uso substitui o valor pelo hash em seguida.
     */
    public static User toDomain(RegisterRequest request) {
        return User.create(request.name(), request.email(), request.password(), request.phone());
    }

    public static AuthResponse toResponse(User user, String accessToken, String refreshToken) {
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessToken,
                refreshToken
        );
    }
}
