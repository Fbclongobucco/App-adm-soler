package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.application.mapper.ProjectDtoMapper;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;

import java.util.UUID;

public class ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public ProjectUseCase(ProjectRepository projectRepository, ClientRepository clientRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
    }

    public ProjectResponse create(ProjectRequest request) {
        Client client = findClient(request.clientId());
        Project saved = projectRepository.save(ProjectDtoMapper.toDomain(request, client));
        return ProjectDtoMapper.toResponse(saved);
    }

    public ProjectResponse findById(UUID id) {
        return ProjectDtoMapper.toResponse(findProject(id));
    }

    public PageResponse<ProjectResponse> findAll(int page, int size) {
        PageResult<Project> result = projectRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, ProjectDtoMapper::toResponse);
    }

    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = findProject(id);
        Client client = findClient(request.clientId());
        ProjectDtoMapper.applyTo(project, request, client);
        return ProjectDtoMapper.toResponse(projectRepository.save(project));
    }

    public void delete(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Projeto nao encontrado com id: " + id);
        }
        projectRepository.deleteById(id);
    }

    private Project findProject(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + id));
    }

    private Client findClient(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + clientId));
    }
}
