package com.buccodev.adm_soler.application.dto.client;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponseDto(UUID id, String name, String email, String phone, String cnpj,
                                UUID addressId, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
