package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;

import java.util.List;
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
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + request.projectId()));
        Accommodation accommodation = request.toDomain(address, project);
        Accommodation saved = accommodationRepository.save(accommodation);
        return AccommodationResponse.fromDomain(saved);
    }

    public AccommodationResponse findById(UUID id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acomodacao nao encontrada com id: " + id));
        return AccommodationResponse.fromDomain(accommodation);
    }

    public List<AccommodationResponse> findAll() {
        return accommodationRepository.findAll().stream()
                .map(AccommodationResponse::fromDomain)
                .toList();
    }

    public AccommodationResponse update(UUID id, AccommodationRequest request) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acomodacao nao encontrada com id: " + id));
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + request.projectId()));
        accommodation.setAddress(address);
        accommodation.setProject(project);
        accommodation.setCapacity(request.capacity());
        accommodation.reschedule(request.startDate(), request.endDate());
        Accommodation updated = accommodationRepository.save(accommodation);
        return AccommodationResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!accommodationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Acomodacao nao encontrada com id: " + id);
        }
        accommodationRepository.deleteById(id);
    }
}
