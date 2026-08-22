package com.buccodev.adm_soler.application.dto.client;

import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.core.domain.Client;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ClientResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String cnpj,
        AddressResponse address,
        Set<UUID> projectIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ClientResponse fromDomain(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCnpj(),
                client.getAddress() != null ? AddressResponse.fromDomain(client.getAddress()) : null,
                client.getProjects() != null
                        ? client.getProjects().stream().map(p -> p.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}
