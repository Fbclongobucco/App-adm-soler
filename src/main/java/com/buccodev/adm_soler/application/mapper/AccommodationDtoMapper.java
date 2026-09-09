package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.domain.Project;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AccommodationDtoMapper {

    private AccommodationDtoMapper() {
    }

    public static Accommodation toDomain(AccommodationRequest request, Address address, Project project) {
        return Accommodation.create(
                address,
                project,
                request.capacity(),
                request.startDate(),
                request.endDate()
        );
    }

    public static void applyTo(Accommodation accommodation, AccommodationRequest request, Address address) {
        accommodation.setAddress(address);
        accommodation.setCapacity(request.capacity());
        accommodation.setStartDate(request.startDate());
        accommodation.setEndDate(request.endDate());
    }

    public static AccommodationResponse toResponse(Accommodation accommodation) {
        if (accommodation == null) {
            return null;
        }
        return new AccommodationResponse(
                accommodation.getId(),
                accommodation.getAddress() != null ? accommodation.getAddress().getId() : null,
                accommodation.getProject() != null ? accommodation.getProject().getId() : null,
                accommodation.getCapacity(),
                accommodation.getStartDate(),
                accommodation.getEndDate(),
                employeeIds(accommodation.getEmployees()),
                accommodation.getCreatedAt(),
                accommodation.getUpdatedAt()
        );
    }

    private static Set<UUID> employeeIds(Set<Employee> employees) {
        if (employees == null) {
            return Collections.emptySet();
        }
        return employees.stream().map(Employee::getId).collect(Collectors.toSet());
    }
}
