package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequestDto;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponseDto;
import com.buccodev.adm_soler.application.exception.AccommodationNotFoundException;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.application.mapper.AccommodationMapper;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.UUID;

public class AccommodationUseCase {

    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final ProjectRepository projectRepository;

    public AccommodationUseCase(AccommodationRepository accommodationRepository,
                                AddressRepository addressRepository,
                                ProjectRepository projectRepository) {
        this.accommodationRepository = accommodationRepository;
        this.addressRepository = addressRepository;
        this.projectRepository = projectRepository;
    }

    public AccommodationResponseDto createAccommodation(AccommodationRequestDto request) {
        requireAddress(request.addressId());
        requireProject(request.projectId());
        Accommodation saved = accommodationRepository.save(AccommodationMapper.toDomain(request));
        return AccommodationMapper.toResponseDto(saved);
    }

    public AccommodationResponseDto getAccommodationById(UUID id) {
        return AccommodationMapper.toResponseDto(findAccommodation(id));
    }

    public PageResponseDto<AccommodationResponseDto> listAccommodations(int page, int size) {
        var result = accommodationRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, AccommodationMapper::toResponseDto);
    }

    public AccommodationResponseDto updateAccommodation(UUID id, AccommodationRequestDto request) {
        Accommodation accommodation = findAccommodation(id);
        requireAddress(request.addressId());
        accommodation.update(request.addressId(), request.capacity(), request.startDate(),
                request.endDate());
        return AccommodationMapper.toResponseDto(accommodationRepository.save(accommodation));
    }

    public void deleteAccommodation(UUID id) {
        accommodationRepository.delete(findAccommodation(id));
    }

    private Accommodation findAccommodation(UUID id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> AccommodationNotFoundException.withId(id));
    }

    private void requireAddress(UUID addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw AddressNotFoundException.withId(addressId);
        }
    }

    private void requireProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException.withId(projectId);
        }
    }
}
