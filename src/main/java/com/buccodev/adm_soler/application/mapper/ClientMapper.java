package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.client.ClientRequestDto;
import com.buccodev.adm_soler.application.dto.client.ClientResponseDto;
import com.buccodev.adm_soler.core.domain.Client;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static Client toDomain(ClientRequestDto dto) {
        return Client.create(dto.name(), dto.email(), dto.phone(), dto.cnpj(), dto.addressId());
    }

    public static ClientResponseDto toResponseDto(Client client) {
        return new ClientResponseDto(client.getId(), client.getName(), client.getEmail(),
                client.getPhone(), client.getCnpj(), client.getAddressId(),
                client.getCreatedAt(), client.getUpdatedAt());
    }
}
