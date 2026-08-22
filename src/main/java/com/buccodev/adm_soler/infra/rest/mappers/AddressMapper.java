package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.infra.rest.entities.AddressJpa;

public class AddressMapper {

    public static AddressJpa toJpa(Address domain) {
        if (domain == null) return null;
        return new AddressJpa(
                domain.getId(),
                domain.getStreet(),
                domain.getNumber(),
                domain.getComplement(),
                domain.getNeighborhood(),
                domain.getCity(),
                domain.getState(),
                domain.getZipCode(),
                domain.getCountry(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    public static Address toDomain(AddressJpa jpa) {
        if (jpa == null) return null;
        return Address.restore(
                jpa.getId(),
                jpa.getStreet(),
                jpa.getNumber(),
                jpa.getComplement(),
                jpa.getNeighborhood(),
                jpa.getCity(),
                jpa.getState(),
                jpa.getZipCode(),
                jpa.getCountry(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
