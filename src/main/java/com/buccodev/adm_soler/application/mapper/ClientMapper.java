package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientMapper {

    public static Client toDomain(ClientRequest request, Address address) {
        return Client.create(
                request.name(),
                request.email(),
                request.phone(),
                request.cnpj(),
                address
        );
    }

    public static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCnpj(),
                client.getAddress() != null ? AddressMapper.toResponse(client.getAddress()) : null,
                client.getProjects() != null
                        ? client.getProjects().stream().map(p -> p.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}
