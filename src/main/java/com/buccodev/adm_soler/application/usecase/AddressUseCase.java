package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.address.AddressRequest;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.repository.AddressRepository;

import java.util.List;
import java.util.UUID;

public class AddressUseCase {

    private final AddressRepository addressRepository;

    public AddressUseCase(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponse create(AddressRequest request) {
        Address address = request.toDomain();
        Address saved = addressRepository.save(address);
        return AddressResponse.fromDomain(saved);
    }

    public AddressResponse findById(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + id));
        return AddressResponse.fromDomain(address);
    }

    public List<AddressResponse> findAll() {
        return addressRepository.findAll().stream()
                .map(AddressResponse::fromDomain)
                .toList();
    }

    public AddressResponse update(UUID id, AddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + id));
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        address.setCountry(request.country());
        Address updated = addressRepository.save(address);
        return AddressResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Endereco nao encontrado com id: " + id);
        }
        addressRepository.deleteById(id);
    }
}
