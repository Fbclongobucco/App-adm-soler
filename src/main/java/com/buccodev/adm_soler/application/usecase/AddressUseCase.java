package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.address.AddressRequest;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.AddressMapper;
import com.buccodev.adm_soler.core.repository.AddressRepository;

import java.util.List;
import java.util.UUID;

public class AddressUseCase {

    private final AddressRepository addressRepository;

    public AddressUseCase(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponse create(AddressRequest request) {
        var address = AddressMapper.toDomain(request);
        return AddressMapper.toResponse(addressRepository.save(address));
    }

    public AddressResponse findById(UUID id) {
        var address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        return AddressMapper.toResponse(address);
    }

    public List<AddressResponse> findAll() {
        return addressRepository.findAll().stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    public AddressResponse update(UUID id, AddressRequest request) {
        findById(id);
        var address = AddressMapper.toDomain(request);
        return AddressMapper.toResponse(addressRepository.save(address));
    }

    public void delete(UUID id) {
        findById(id);
        addressRepository.deleteById(id);
    }
}
