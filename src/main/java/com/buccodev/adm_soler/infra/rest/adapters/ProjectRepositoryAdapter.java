package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.infra.rest.entities.ProjectEntity;
import com.buccodev.adm_soler.infra.rest.jpa_repository.ProjectJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;

    public ProjectRepositoryAdapter(ProjectJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Project save(Project project) {
        return toDomain(jpaRepository.save(toEntity(project)));
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(ProjectRepositoryAdapter::toDomain);
    }

    @Override
    public Repository.PageResult<Project> findAll(Repository.PageQuery pageQuery) {
        Page<ProjectEntity> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new Repository.PageResult<>(
                page.getContent().stream().map(ProjectRepositoryAdapter::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public void delete(Project project) {
        jpaRepository.deleteById(project.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    static ProjectEntity toEntity(Project project) {
        return new ProjectEntity(
                project.getId(),
                project.getOs(),
                project.getServiceProvided(),
                project.getClientId(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    static Project toDomain(ProjectEntity entity) {
        return Project.restore(
                entity.getId(),
                entity.getOs(),
                entity.getServiceProvided(),
                entity.getClientId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
