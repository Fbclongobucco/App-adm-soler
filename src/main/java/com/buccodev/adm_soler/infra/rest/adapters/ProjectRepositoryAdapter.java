package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.infra.rest.entities.ProjectJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.ProjectJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.ProjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;

    public ProjectRepositoryAdapter(ProjectJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public Project save(Project project) {
        ProjectJpa jpa = ProjectMapper.toJpa(project);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        ProjectJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return ProjectMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return ProjectMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Project> findAll() {
        return jpaRepository.findAll().stream().map(ProjectMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
