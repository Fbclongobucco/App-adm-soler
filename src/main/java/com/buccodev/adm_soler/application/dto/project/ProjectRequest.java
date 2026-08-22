package com.buccodev.adm_soler.application.dto.project;

import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectRequest(
        String os,
        String serviceProvided,
        UUID clientId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public Project toDomain(Client client) {
        return Project.create(
                os,
                serviceProvided,
                client,
                startDate,
                endDate
        );
    }
}
