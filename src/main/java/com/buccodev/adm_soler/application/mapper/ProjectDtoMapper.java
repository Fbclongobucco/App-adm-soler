package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ProjectDtoMapper {

    private ProjectDtoMapper() {
    }

    public static Project toDomain(ProjectRequest request, Client client) {
        return Project.create(
                request.os(),
                request.serviceProvided(),
                client,
                request.startDate(),
                request.endDate()
        );
    }

    public static void applyTo(Project project, ProjectRequest request, Client client) {
        project.setOs(request.os());
        project.setServiceProvided(request.serviceProvided());
        project.setClient(client);
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
    }

    public static ProjectResponse toResponse(Project project) {
        if (project == null) {
            return null;
        }
        return new ProjectResponse(
                project.getId(),
                project.getOs(),
                project.getServiceProvided(),
                project.getClient() != null ? project.getClient().getId() : null,
                ids(project.getRestaurants(), Restaurant::getId),
                ids(project.getAccommodations(), Accommodation::getId),
                ids(project.getEmployees(), Employee::getId),
                ids(project.getEquipments(), Equipment::getId),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    private static <T> Set<UUID> ids(Set<T> items, Function<T, UUID> idExtractor) {
        if (items == null) {
            return Collections.emptySet();
        }
        return items.stream().map(idExtractor).collect(Collectors.toSet());
    }
}
