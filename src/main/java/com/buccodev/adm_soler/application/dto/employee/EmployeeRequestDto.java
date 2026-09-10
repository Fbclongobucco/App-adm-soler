package com.buccodev.adm_soler.application.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmployeeRequestDto(@NotBlank String name,
                                 @Email String email,
                                 String phone,
                                 @NotNull UUID addressId,
                                 @NotBlank String role) {}
