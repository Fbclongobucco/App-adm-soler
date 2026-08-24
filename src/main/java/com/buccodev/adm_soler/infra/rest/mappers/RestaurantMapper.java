package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.infra.rest.entities.RestaurantJpa;

public class RestaurantMapper {

    public static RestaurantJpa toJpa(Restaurant domain) {
        if (domain == null) return null;
        RestaurantJpa jpa = new RestaurantJpa();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setEmail(domain.getEmail());
        jpa.setPhone(domain.getPhone());
        jpa.setCnpj(domain.getCnpj());
        jpa.setProject(ProjectMapper.toJpa(domain.getProject()));
        jpa.setIsBilled(domain.getIsBilled());
        jpa.setLunchPrice(domain.getLunchPrice());
        jpa.setDinnerPrice(domain.getDinnerPrice());
        jpa.setTotal(domain.getTotal());
        jpa.setAdditionalValues(domain.getAdditionalValues());
        jpa.setValuePerEmployee(domain.getValuePerEmployee());
        jpa.setDays(domain.getDays());
        jpa.setAddress(AddressMapper.toJpa(domain.getAddress()));
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static Restaurant toDomain(RestaurantJpa jpa) {
        if (jpa == null) return null;
        return Restaurant.restore(
                jpa.getId(),
                jpa.getName(),
                jpa.getEmail(),
                jpa.getPhone(),
                jpa.getCnpj(),
                ProjectMapper.toDomain(jpa.getProject()),
                null,
                jpa.getIsBilled(),
                jpa.getLunchPrice(),
                jpa.getDinnerPrice(),
                jpa.getTotal(),
                jpa.getAdditionalValues(),
                jpa.getValuePerEmployee(),
                jpa.getDays(),
                AddressMapper.toDomain(jpa.getAddress()),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
