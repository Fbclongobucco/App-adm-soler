package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.user.UserRequestDto;
import com.buccodev.adm_soler.core.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toDomainBuildsARegularUserWithTheGivenPassword() {
        User user = UserMapper.toDomain(new UserRequestDto("Joao", "joao@email.com",
                "already-hashed", "11987654321"));

        assertEquals(User.Role.USER, user.getRole());
        assertEquals("already-hashed", user.getPassword());
    }

    @Test
    void toResponseDtoNeverExposesThePassword() {
        User user = User.create("Joao", "joao@email.com", "hashed", "11987654321");

        var dto = UserMapper.toResponseDto(user);

        assertEquals(user.getId(), dto.id());
        assertEquals("joao@email.com", dto.email());
        assertEquals(User.Role.USER, dto.role());
        assertFalse(dto.toString().contains("hashed"));
    }
}
