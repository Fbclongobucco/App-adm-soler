package com.buccodev.adm_soler.application.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClientRequestDto(@NotBlank String name,
                               @Email String email,
                               String phone,
                               String cnpj,
                               @NotNull UUID addressId) {}
