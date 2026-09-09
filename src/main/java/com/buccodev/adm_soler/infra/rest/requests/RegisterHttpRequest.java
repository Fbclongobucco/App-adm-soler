package com.buccodev.adm_soler.infra.rest.requests;

import com.buccodev.adm_soler.application.dto.auth.RegisterRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterHttpRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, message = "password must be at least 6 characters")
        String password,

        String phone
) {
    public RegisterRequest toApplicationRequest() {
        return new RegisterRequest(name, email, password, phone);
    }
}
