package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.core.domain.User;

public class UserMapper {

    public static User toDomain(UserRequest request) {
        return User.create(
                request.name(),
                request.email(),
                request.password(),
                request.phone()
        );
    }

    public static UserResponse toResponse(User user) {
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
