package com.buccodev.adm_soler.application.dto.address;

import jakarta.validation.constraints.NotBlank;

public record AddressRequestDto(@NotBlank String street,
                                String number,
                                String complement,
                                String neighborhood,
                                @NotBlank String city,
                                @NotBlank String state,
                                @NotBlank String zipCode,
                                @NotBlank String country) {}
