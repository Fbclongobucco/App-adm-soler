package com.buccodev.adm_soler.application.dto.client;

import com.buccodev.adm_soler.application.dto.address.AddressResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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
}
