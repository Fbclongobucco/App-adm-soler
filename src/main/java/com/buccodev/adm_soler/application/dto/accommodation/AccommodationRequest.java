package com.buccodev.adm_soler.application.dto.accommodation;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccommodationRequest(
        UUID addressId,
        UUID projectId,
        Integer capacity,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public Accommodation toDomain(Address address, Project project) {
        return Accommodation.create(
                address,
                project,
                capacity,
                startDate,
                endDate
        );
    }
}
