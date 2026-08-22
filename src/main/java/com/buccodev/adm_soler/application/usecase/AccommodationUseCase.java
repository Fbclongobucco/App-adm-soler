package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.AccommodationMapper;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;

import java.util.List;
import java.util.UUID;

public class AccommodationUseCase {

    private final AccommodationRepository accommodationRepository;
    private final AddressRepository addressRepository;
    private final ProjectRepository projectRepository;

    public AccommodationUseCase(AccommodationRepository accommodationRepository, AddressRepository addressRepository,
                                ProjectRepository projectRepository) {
        this.accommodationRepository = accommodationRepository;
        this.addressRepository = addressRepository;
        this.projectRepository = projectRepository;
    }

    public AccommodationResponse create(AccommodationRequest request) {
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        var accommodation = AccommodationMapper.toDomain(request, address, project);
        return AccommodationMapper.toResponse(accommodationRepository.save(accommodation));
    }

    public AccommodationResponse findById(UUID id) {
        var accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));
        return AccommodationMapper.toResponse(accommodation);
    }

    public List<AccommodationResponse> findAll() {
        return accommodationRepository.findAll().stream()
                .map(AccommodationMapper::toResponse)
                .toList();
    }

    public AccommodationResponse update(UUID id, AccommodationRequest request) {
        findById(id);
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        var accommodation = AccommodationMapper.toDomain(request, address, project);
        return AccommodationMapper.toResponse(accommodationRepository.save(accommodation));
    }

    public void delete(UUID id) {
        findById(id);
        accommodationRepository.deleteById(id);
    }
}
