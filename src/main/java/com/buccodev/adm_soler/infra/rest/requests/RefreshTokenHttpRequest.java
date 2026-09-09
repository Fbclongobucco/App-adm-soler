package com.buccodev.adm_soler.infra.rest.requests;

import com.buccodev.adm_soler.application.dto.auth.RefreshTokenRequest;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenHttpRequest(
        @NotBlank(message = "refreshToken is required")
        String refreshToken
) {
    public RefreshTokenRequest toApplicationRequest() {
        return new RefreshTokenRequest(refreshToken);
    }
}
