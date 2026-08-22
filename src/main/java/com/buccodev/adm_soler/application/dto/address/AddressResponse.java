package com.buccodev.adm_soler.application.dto.address;

import java.time.LocalDateTime;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
