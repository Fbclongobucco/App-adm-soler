package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.project.ProjectRequestDto;
import com.buccodev.adm_soler.application.dto.project.ProjectResponseDto;
import com.buccodev.adm_soler.core.domain.Project;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static Project toDomain(ProjectRequestDto dto) {
        return Project.create(dto.os(), dto.serviceProvided(), dto.clientId(),
                dto.startDate(), dto.endDate());
    }

    public static ProjectResponseDto toResponseDto(Project project) {
        return new ProjectResponseDto(project.getId(), project.getOs(), project.getServiceProvided(),
                project.getClientId(), project.getStartDate(), project.getEndDate(),
                project.getCreatedAt(), project.getUpdatedAt());
    }
}
