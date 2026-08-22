package com.buccodev.adm_soler.application.dto.client;

import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;

import java.util.UUID;

public record ClientRequest(
        String name,
        String email,
        String phone,
        String cnpj,
        UUID addressId
) {
    public Client toDomain(Address address) {
        return Client.create(
                name,
                email,
                phone,
                cnpj,
                address
        );
    }
}
