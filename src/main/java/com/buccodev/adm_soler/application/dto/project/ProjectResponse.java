package com.buccodev.adm_soler.application.dto.project;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String os,
        String serviceProvided,
        UUID clientId,
        Set<UUID> restaurantIds,
        Set<UUID> accommodationIds,
        Set<UUID> employeeIds,
        Set<UUID> equipmentIds,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
