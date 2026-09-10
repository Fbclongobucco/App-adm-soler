package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.user.UserRequestDto;
import com.buccodev.adm_soler.application.dto.user.UserResponseDto;
import com.buccodev.adm_soler.core.domain.User;

public final class UserMapper {

    private UserMapper() {
    }

    /**
     * Espera o DTO com a senha ja transformada em hash pelo caso de uso.
     */
    public static User toDomain(UserRequestDto dto) {
        return User.create(dto.name(), dto.email(), dto.password(), dto.phone());
    }

    public static UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                user.getRole(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
