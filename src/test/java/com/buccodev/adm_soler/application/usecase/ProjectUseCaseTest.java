package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.project.ProjectRequestDto;
import com.buccodev.adm_soler.application.exception.ClientNotFoundException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectUseCaseTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    private ProjectUseCase projectUseCase;
    private UUID clientId;
    private Project sampleProject;

    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 3, 1, 18, 0);

    @BeforeEach
    void setUp() {
        projectUseCase = new ProjectUseCase(projectRepository, clientRepository);
        clientId = UUID.randomUUID();
        sampleProject = Project.create("OS-1024", "Retrofit de cobertura", clientId, START, END);
    }

    private ProjectRequestDto request(String os) {
        return new ProjectRequestDto(os, "Retrofit de cobertura", clientId, START, END);
    }

    @Test
    void createProjectSavesAndReturnsIt() {
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("OS-1024", projectUseCase.createProject(request("OS-1024")).os());
    }

    @Test
    void createProjectThrowsWhenClientIsMissing() {
        when(clientRepository.existsById(clientId)).thenReturn(false);

        assertThrows(ClientNotFoundException.class,
                () -> projectUseCase.createProject(request("OS-1024")));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void getProjectByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectUseCase.getProjectById(id));
    }

    @Test
    void listProjectsMapsThePage() {
        when(projectRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleProject), 0, 20, 1, 1));

        assertEquals(1, projectUseCase.listProjects(0, 20).content().size());
    }

    @Test
    void updateProjectAppliesTheChange() {
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("OS-2048", projectUseCase.updateProject(sampleProject.getId(),
                request("OS-2048")).os());
    }

    @Test
    void deleteProjectRemovesTheEntity() {
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));

        projectUseCase.deleteProject(sampleProject.getId());

        verify(projectRepository).delete(sampleProject);
    }
}
