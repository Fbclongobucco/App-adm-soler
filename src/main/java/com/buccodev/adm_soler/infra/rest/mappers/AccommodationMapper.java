package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.infra.rest.entities.AccommodationJpa;

public class AccommodationMapper {

    public static AccommodationJpa toJpa(Accommodation domain) {
        if (domain == null) return null;
        AccommodationJpa jpa = new AccommodationJpa();
        jpa.setId(domain.getId());
        jpa.setAddress(AddressMapper.toJpa(domain.getAddress()));
        jpa.setProject(ProjectMapper.toJpa(domain.getProject()));
        jpa.setCapacity(domain.getCapacity());
        jpa.setStartDate(domain.getStartDate());
        jpa.setEndDate(domain.getEndDate());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static Accommodation toDomain(AccommodationJpa jpa) {
        if (jpa == null) return null;
        return Accommodation.restore(
                jpa.getId(),
                AddressMapper.toDomain(jpa.getAddress()),
                ProjectMapper.toDomain(jpa.getProject()),
                jpa.getCapacity(),
                jpa.getStartDate(),
                jpa.getEndDate(),
                null,
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
