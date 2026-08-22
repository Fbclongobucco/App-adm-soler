package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.ProjectMapper;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;

import java.util.List;
import java.util.UUID;

public class ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public ProjectUseCase(ProjectRepository projectRepository, ClientRepository clientRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
    }

    public ProjectResponse create(ProjectRequest request) {
        var client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        var project = ProjectMapper.toDomain(request);
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    public ProjectResponse findById(UUID id) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return ProjectMapper.toResponse(project);
    }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    public ProjectResponse update(UUID id, ProjectRequest request) {
        findById(id);
        var project = ProjectMapper.toDomain(request);
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    public void delete(UUID id) {
        findById(id);
        projectRepository.deleteById(id);
    }
}
