package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.address.AddressRequest;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.core.domain.Address;

public final class AddressDtoMapper {

    private AddressDtoMapper() {
    }

    public static Address toDomain(AddressRequest request) {
        return Address.create(
                request.street(),
                request.number(),
                request.complement(),
                request.neighborhood(),
                request.city(),
                request.state(),
                request.zipCode(),
                request.country()
        );
    }

    public static void applyTo(Address address, AddressRequest request) {
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        address.setCountry(request.country());
    }

    public static AddressResponse toResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}
