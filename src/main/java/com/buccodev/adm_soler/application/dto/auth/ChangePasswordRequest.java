package com.buccodev.adm_soler.application.dto.auth;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
