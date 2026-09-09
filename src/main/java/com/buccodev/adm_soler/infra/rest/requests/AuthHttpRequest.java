package com.buccodev.adm_soler.infra.rest.requests;

import com.buccodev.adm_soler.application.dto.auth.AuthRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthHttpRequest(
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "password is required")
        String password
) {
    public AuthRequest toApplicationRequest() {
        return new AuthRequest(email, password);
    }
}
