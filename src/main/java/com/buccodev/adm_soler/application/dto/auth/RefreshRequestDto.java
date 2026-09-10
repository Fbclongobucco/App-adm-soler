package com.buccodev.adm_soler.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(@NotBlank String refreshToken) {}
