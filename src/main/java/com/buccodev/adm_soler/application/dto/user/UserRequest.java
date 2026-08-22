package com.buccodev.adm_soler.application.dto.user;

public record UserRequest(
        String name,
        String email,
        String password,
        String phone
) {
}
