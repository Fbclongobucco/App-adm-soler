package com.buccodev.adm_soler.application.dto.project;

import com.buccodev.adm_soler.core.domain.Project;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public static ProjectResponse fromDomain(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getOs(),
                project.getServiceProvided(),
                project.getClient() != null ? project.getClient().getId() : null,
                project.getRestaurants() != null
                        ? project.getRestaurants().stream().map(r -> r.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                project.getAccommodations() != null
                        ? project.getAccommodations().stream().map(a -> a.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                project.getEmployees() != null
                        ? project.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                project.getEquipments() != null
                        ? project.getEquipments().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
