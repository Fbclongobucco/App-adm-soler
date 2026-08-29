package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
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

    public PageResponse<AccommodationResponse> findAll(int page, int size) {
        PageResult<Accommodation> result = accommodationRepository.findAll(new PageQuery(page, size));
        return PageResponse.from(result, AccommodationResponse::fromDomain);
    }

    public AccommodationResponse update(UUID id, AccommodationRequest request) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acomodacao nao encontrada com id: " + id));
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        accommodation.setAddress(address);
        accommodation.setCapacity(request.capacity());
        accommodation.setStartDate(request.startDate());
        accommodation.setEndDate(request.endDate());
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
