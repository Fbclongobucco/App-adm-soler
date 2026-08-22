package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.infra.rest.entities.ProjectJpa;

public class ProjectMapper {

    public static ProjectJpa toJpa(Project domain) {
        if (domain == null) return null;
        ProjectJpa jpa = new ProjectJpa();
        jpa.setId(domain.getId());
        jpa.setOs(domain.getOs());
        jpa.setServiceProvided(domain.getServiceProvided());
        jpa.setClient(ClientMapper.toJpa(domain.getClient()));
        jpa.setStartDate(domain.getStartDate());
        jpa.setEndDate(domain.getEndDate());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static Project toDomain(ProjectJpa jpa) {
        if (jpa == null) return null;
        return Project.restore(
                jpa.getId(),
                jpa.getOs(),
                jpa.getServiceProvided(),
                ClientMapper.toDomain(jpa.getClient()),
                null,
                null,
                null,
                null,
                jpa.getStartDate(),
                jpa.getEndDate(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
