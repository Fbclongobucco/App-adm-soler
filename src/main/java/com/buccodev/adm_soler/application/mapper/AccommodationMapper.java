package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequestDto;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponseDto;
import com.buccodev.adm_soler.core.domain.Accommodation;

public final class AccommodationMapper {

    private AccommodationMapper() {
    }

    public static Accommodation toDomain(AccommodationRequestDto dto) {
        return Accommodation.create(dto.addressId(), dto.projectId(), dto.capacity(),
                dto.startDate(), dto.endDate());
    }

    public static AccommodationResponseDto toResponseDto(Accommodation accommodation) {
        return new AccommodationResponseDto(accommodation.getId(), accommodation.getAddressId(),
                accommodation.getProjectId(), accommodation.getCapacity(),
                accommodation.getStartDate(), accommodation.getEndDate(),
                accommodation.getCreatedAt(), accommodation.getUpdatedAt());
    }
}
