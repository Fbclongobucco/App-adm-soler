package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.core.domain.User;

public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    /**
     * Monta o dominio com a senha em claro para que as invariantes de {@link User}
     * a validem. O caso de uso substitui o valor pelo hash em seguida.
     */
    public static User toDomain(UserRequest request) {
        return User.create(request.name(), request.email(), request.password(), request.phone());
    }

    public static void applyTo(User user, UserRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setPhone(request.phone());
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
