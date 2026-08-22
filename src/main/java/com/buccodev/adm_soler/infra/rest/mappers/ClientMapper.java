package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.infra.rest.entities.ClientJpa;

public class ClientMapper {

    public static ClientJpa toJpa(Client domain) {
        if (domain == null) return null;
        ClientJpa jpa = new ClientJpa();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setEmail(domain.getEmail());
        jpa.setPhone(domain.getPhone());
        jpa.setCnpj(domain.getCnpj());
        jpa.setAddress(AddressMapper.toJpa(domain.getAddress()));
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static Client toDomain(ClientJpa jpa) {
        if (jpa == null) return null;
        return Client.restore(
                jpa.getId(),
                jpa.getName(),
                jpa.getEmail(),
                jpa.getPhone(),
                jpa.getCnpj(),
                AddressMapper.toDomain(jpa.getAddress()),
                null,
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
