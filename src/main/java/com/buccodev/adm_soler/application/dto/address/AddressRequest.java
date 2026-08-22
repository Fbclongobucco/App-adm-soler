package com.buccodev.adm_soler.application.dto.address;

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
}
