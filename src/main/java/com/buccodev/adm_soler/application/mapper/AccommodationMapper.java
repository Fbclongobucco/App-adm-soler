package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;

import java.util.Collections;
import java.util.stream.Collectors;

public class AccommodationMapper {

    public static Accommodation toDomain(AccommodationRequest request, Address address, Project project) {
        return Accommodation.create(
                address,
                project,
                request.capacity(),
                request.startDate(),
                request.endDate()
        );
    }

    public static AccommodationResponse toResponse(Accommodation accommodation) {
        return new AccommodationResponse(
                accommodation.getId(),
                accommodation.getAddress() != null ? accommodation.getAddress().getId() : null,
                accommodation.getProject() != null ? accommodation.getProject().getId() : null,
                accommodation.getCapacity(),
                accommodation.getStartDate(),
                accommodation.getEndDate(),
                accommodation.getEmployees() != null
                        ? accommodation.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
                accommodation.getCreatedAt(),
                accommodation.getUpdatedAt()
        );
    }
}
