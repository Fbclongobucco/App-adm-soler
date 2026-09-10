package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.project.ProjectRequestDto;
import com.buccodev.adm_soler.application.dto.project.ProjectResponseDto;
import com.buccodev.adm_soler.application.exception.ClientNotFoundException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.application.mapper.ProjectMapper;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.UUID;

public class ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public ProjectUseCase(ProjectRepository projectRepository, ClientRepository clientRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
    }

    public ProjectResponseDto createProject(ProjectRequestDto request) {
        requireClient(request.clientId());
        Project saved = projectRepository.save(ProjectMapper.toDomain(request));
        return ProjectMapper.toResponseDto(saved);
    }

    public ProjectResponseDto getProjectById(UUID id) {
        return ProjectMapper.toResponseDto(findProject(id));
    }

    public PageResponseDto<ProjectResponseDto> listProjects(int page, int size) {
        var result = projectRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, ProjectMapper::toResponseDto);
    }

    public ProjectResponseDto updateProject(UUID id, ProjectRequestDto request) {
        Project project = findProject(id);
        requireClient(request.clientId());
        project.update(request.os(), request.serviceProvided(), request.clientId(),
                request.startDate(), request.endDate());
        return ProjectMapper.toResponseDto(projectRepository.save(project));
    }

    public void deleteProject(UUID id) {
        projectRepository.delete(findProject(id));
    }

    private Project findProject(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> ProjectNotFoundException.withId(id));
    }

    private void requireClient(UUID clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw ClientNotFoundException.withId(clientId);
        }
    }
}
