package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.project.ProjectRequestDto;
import com.buccodev.adm_soler.application.dto.project.ProjectResponseDto;
import com.buccodev.adm_soler.application.usecase.ProjectUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Projects", description = "Obras, identificadas pela OS e vinculadas a um cliente")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectUseCase projectUseCase;

    public ProjectController(ProjectUseCase projectUseCase) {
        this.projectUseCase = projectUseCase;
    }

    @Operation(summary = "Cria um projeto", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<ProjectResponseDto> create(@Valid @RequestBody ProjectRequestDto request) {
        ProjectResponseDto created = projectUseCase.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um projeto por id")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectUseCase.getProjectById(id));
    }

    @Operation(summary = "Lista projetos paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<ProjectResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(projectUseCase.listProjects(page, size));
    }

    @Operation(summary = "Atualiza um projeto", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody ProjectRequestDto request) {
        return ResponseEntity.ok(projectUseCase.updateProject(id, request));
    }

    @Operation(summary = "Remove um projeto", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        projectUseCase.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
