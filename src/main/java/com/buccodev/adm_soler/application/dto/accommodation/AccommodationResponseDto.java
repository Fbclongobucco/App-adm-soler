package com.buccodev.adm_soler.application.dto.accommodation;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccommodationResponseDto(UUID id, UUID addressId, UUID projectId, Integer capacity,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
}
