package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.project.ProjectRequest;
import com.buccodev.adm_soler.application.dto.project.ProjectResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.ProjectUseCase;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectUseCaseTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ProjectUseCase projectUseCase;

    private Client sampleClient;
    private Project sampleProject;

    @BeforeEach
    void setUp() {
        Address sampleAddress = Address.restore(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleClient = Client.restore(
                UUID.randomUUID(), "Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress, Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleProject = Project.restore(
                UUID.randomUUID(), "OS-001", "Servico X", sampleClient,
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateProject() {
        ProjectRequest request = new ProjectRequest("OS-001", "Servico X", sampleClient.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        when(clientRepository.findById(sampleClient.getId())).thenReturn(Optional.of(sampleClient));
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);

        ProjectResponse response = projectUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.os()).isEqualTo("OS-001");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentClient() {
        UUID clientId = UUID.randomUUID();
        ProjectRequest request = new ProjectRequest("OS-001", "Servico X", clientId,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindById() {
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));

        ProjectResponse response = projectUseCase.findById(sampleProject.getId());

        assertThat(response.os()).isEqualTo("OS-001");
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(projectRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleProject), 0, 20, 1, 1));

        PageResponse<ProjectResponse> responses = projectUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        ProjectRequest request = new ProjectRequest("OS-002", "Servico Y", sampleClient.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(60));
        Project updated = Project.restore(
                sampleProject.getId(), "OS-002", "Servico Y", sampleClient,
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(60),
                sampleProject.getCreatedAt(), LocalDateTime.now()
        );
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));
        when(clientRepository.findById(sampleClient.getId())).thenReturn(Optional.of(sampleClient));
        when(projectRepository.save(any(Project.class))).thenReturn(updated);

        ProjectResponse response = projectUseCase.update(sampleProject.getId(), request);

        assertThat(response.os()).isEqualTo("OS-002");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        ProjectRequest request = new ProjectRequest("OS-002", "Servico Y", sampleClient.getId(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(60));
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectUseCase.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        when(projectRepository.existsById(sampleProject.getId())).thenReturn(true);
        doNothing().when(projectRepository).deleteById(sampleProject.getId());

        projectUseCase.delete(sampleProject.getId());

        verify(projectRepository).deleteById(sampleProject.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> projectUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
