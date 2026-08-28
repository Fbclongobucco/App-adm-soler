package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final RestaurantRepository restaurantRepository;
    private final AccommodationRepository accommodationRepository;
    private final EmployeeRepository employeeRepository;
    private final EquipmentRepository equipmentRepository;

    public ProjectUseCase(ProjectRepository projectRepository,
                          ClientRepository clientRepository,
                          RestaurantRepository restaurantRepository,
                          AccommodationRepository accommodationRepository,
                          EmployeeRepository employeeRepository,
                          EquipmentRepository equipmentRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.restaurantRepository = restaurantRepository;
        this.accommodationRepository = accommodationRepository;
        this.employeeRepository = employeeRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public ProjectResponse create(ProjectRequest request) {
        Client client = requireClient(request.clientId());
        Project project = request.toDomain(client);

        replaceEmployees(project, request.employeeIds());
        replaceEquipments(project, request.equipmentIds());

        Project saved = projectRepository.save(project);
        linkChildren(saved, request);
        return ProjectResponse.fromDomain(reload(saved.getId()));
    }

    public ProjectResponse findById(UUID id) {
        return ProjectResponse.fromDomain(reload(id));
    }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::fromDomain)
                .toList();
    }

    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = reload(id);
        Client client = requireClient(request.clientId());

        project.setOs(request.os());
        project.setServiceProvided(request.serviceProvided());
        project.setClient(client);
        project.reschedule(request.startDate(), request.endDate());
        project.setUpdatedAt(LocalDateTime.now());

        replaceEmployees(project, request.employeeIds());
        replaceEquipments(project, request.equipmentIds());

        Project updated = projectRepository.save(project);
        linkChildren(updated, request);
        return ProjectResponse.fromDomain(reload(updated.getId()));
    }

    public void delete(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Projeto nao encontrado com id: " + id);
        }
        projectRepository.deleteById(id);
    }

    /**
     * Le a obra de novo depois de gravar.
     *
     * O que volta do save e a obra como ela foi enviada: restaurantes e
     * alojamentos moram no filho e acabaram de ser reapontados, entao so uma
     * releitura mostra a obra completa na resposta.
     */
    private Project reload(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + id));
    }

    private Client requireClient(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + clientId));
    }

    /** Conjunto ausente significa "nao mexa"; enviado, substitui o vinculo. */
    private void replaceEmployees(Project project, Set<UUID> employeeIds) {
        if (employeeIds == null) return;
        new HashSet<>(project.getEmployees()).forEach(project::removeEmployee);
        for (UUID employeeId : employeeIds) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado com id: " + employeeId));
            project.addEmployee(employee);
        }
    }

    private void replaceEquipments(Project project, Set<UUID> equipmentIds) {
        if (equipmentIds == null) return;
        new HashSet<>(project.getEquipments()).forEach(project::removeEquipment);
        for (UUID equipmentId : equipmentIds) {
            Equipment equipment = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Equipamento nao encontrado com id: " + equipmentId));
            project.addEquipment(equipment);
        }
    }

    /**
     * Restaurantes e alojamentos guardam a obra do lado deles, entao vincular e
     * gravar o filho apontando para esta obra. So vincula: desvincular alojamento
     * nao existe, porque a obra e obrigatoria nele.
     */
    private void linkChildren(Project project, ProjectRequest request) {
        if (request.restaurantIds() != null) {
            for (UUID restaurantId : request.restaurantIds()) {
                Restaurant restaurant = restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Restaurante nao encontrado com id: " + restaurantId));
                restaurant.setProject(project);
                restaurant.setUpdatedAt(LocalDateTime.now());
                restaurantRepository.save(restaurant);
            }
        }
        if (request.accommodationIds() != null) {
            for (UUID accommodationId : request.accommodationIds()) {
                Accommodation accommodation = accommodationRepository.findById(accommodationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Acomodacao nao encontrada com id: " + accommodationId));
                accommodation.setProject(project);
                accommodation.setUpdatedAt(LocalDateTime.now());
                accommodationRepository.save(accommodation);
            }
        }
    }
}
