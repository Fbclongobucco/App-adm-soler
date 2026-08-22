package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
