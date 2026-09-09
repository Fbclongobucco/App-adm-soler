package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.AccommodationDtoMapper;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;

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

    public AccommodationResponse create(AccommodationRequest request) {
        Address address = findAddress(request.addressId());
        Project project = findProject(request.projectId());
        Accommodation saved = accommodationRepository.save(
                AccommodationDtoMapper.toDomain(request, address, project));
        return AccommodationDtoMapper.toResponse(saved);
    }

    public AccommodationResponse findById(UUID id) {
        return AccommodationDtoMapper.toResponse(findAccommodation(id));
    }

    public PageResponse<AccommodationResponse> findAll(int page, int size) {
        PageResult<Accommodation> result = accommodationRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, AccommodationDtoMapper::toResponse);
    }

    public AccommodationResponse update(UUID id, AccommodationRequest request) {
        Accommodation accommodation = findAccommodation(id);
        Address address = findAddress(request.addressId());
        AccommodationDtoMapper.applyTo(accommodation, request, address);
        return AccommodationDtoMapper.toResponse(accommodationRepository.save(accommodation));
    }

    public void delete(UUID id) {
        if (!accommodationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Acomodacao nao encontrada com id: " + id);
        }
        accommodationRepository.deleteById(id);
    }

    private Accommodation findAccommodation(UUID id) {
        return accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acomodacao nao encontrada com id: " + id));
    }

    private Address findAddress(UUID addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + addressId));
    }

    private Project findProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + projectId));
    }
}
