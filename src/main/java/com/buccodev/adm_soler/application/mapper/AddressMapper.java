package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.address.AddressRequestDto;
import com.buccodev.adm_soler.application.dto.address.AddressResponseDto;
import com.buccodev.adm_soler.core.domain.Address;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static Address toDomain(AddressRequestDto dto) {
        return Address.create(dto.street(), dto.number(), dto.complement(), dto.neighborhood(),
                dto.city(), dto.state(), dto.zipCode(), dto.country());
    }

    public static AddressResponseDto toResponseDto(Address address) {
        return new AddressResponseDto(address.getId(), address.getStreet(), address.getNumber(),
                address.getComplement(), address.getNeighborhood(), address.getCity(),
                address.getState(), address.getZipCode(), address.getCountry(),
                address.getCreatedAt(), address.getUpdatedAt());
    }
}
