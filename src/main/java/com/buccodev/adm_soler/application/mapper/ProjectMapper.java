package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.core.domain.Project;

import java.util.Collections;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static Project toDomain(ProjectRequest request) {
        return Project.create(
                request.os(),
                request.serviceProvided(),
                null,
                request.startDate(),
                request.endDate()
        );
    }

    public static ProjectResponse toResponse(Project project) {
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
