package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;
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
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + request.clientId()));
        Project project = request.toDomain(client);
        Project saved = projectRepository.save(project);
        return ProjectResponse.fromDomain(saved);
    }

    public ProjectResponse findById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + id));
        return ProjectResponse.fromDomain(project);
    }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::fromDomain)
                .toList();
    }

    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + id));
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + request.clientId()));
        project.setOs(request.os());
        project.setServiceProvided(request.serviceProvided());
        project.setClient(client);
        project.reschedule(request.startDate(), request.endDate());
        Project updated = projectRepository.save(project);
        return ProjectResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Projeto nao encontrado com id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
