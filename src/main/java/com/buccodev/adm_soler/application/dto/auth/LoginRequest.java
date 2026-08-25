package com.buccodev.adm_soler.application.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
