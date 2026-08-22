package com.buccodev.adm_soler.application.dto.address;

import com.buccodev.adm_soler.core.domain.Address;

public record AddressRequest(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String country
) {
    public Address toDomain() {
        return Address.create(
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                country
        );
    }

    public Address toDomain(java.util.UUID id) {
        return Address.restore(
                id,
                street,
                number,
                complement,
                neighborhood,
                city,
                state,
                zipCode,
                country,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }
}
