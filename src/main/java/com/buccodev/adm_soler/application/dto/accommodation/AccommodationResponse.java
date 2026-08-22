package com.buccodev.adm_soler.application.dto.accommodation;

import com.buccodev.adm_soler.core.domain.Accommodation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public static AccommodationResponse fromDomain(Accommodation accommodation) {
        return new AccommodationResponse(
                accommodation.getId(),
                accommodation.getAddress() != null ? accommodation.getAddress().getId() : null,
                accommodation.getProject() != null ? accommodation.getProject().getId() : null,
                accommodation.getCapacity(),
                accommodation.getStartDate(),
                accommodation.getEndDate(),
                accommodation.getEmployees() != null
                        ? accommodation.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                accommodation.getCreatedAt(),
                accommodation.getUpdatedAt()
        );
    }
}
