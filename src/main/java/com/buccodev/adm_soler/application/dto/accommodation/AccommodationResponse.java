package com.buccodev.adm_soler.application.dto.accommodation;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AccommodationResponse(
        UUID id,
        UUID addressId,
        UUID projectId,
        Integer capacity,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Set<UUID> employeeIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
