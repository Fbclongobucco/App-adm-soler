package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.address.AddressRequest;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.AddressDtoMapper;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.AddressRepository;

import java.util.UUID;

public class AddressUseCase {

    private final AddressRepository addressRepository;

    public AddressUseCase(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponse create(AddressRequest request) {
        Address saved = addressRepository.save(AddressDtoMapper.toDomain(request));
        return AddressDtoMapper.toResponse(saved);
    }

    public AddressResponse findById(UUID id) {
        return AddressDtoMapper.toResponse(findAddress(id));
    }

    public PageResponse<AddressResponse> findAll(int page, int size) {
        PageResult<Address> result = addressRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, AddressDtoMapper::toResponse);
    }

    public AddressResponse update(UUID id, AddressRequest request) {
        Address address = findAddress(id);
        AddressDtoMapper.applyTo(address, request);
        return AddressDtoMapper.toResponse(addressRepository.save(address));
    }

    public void delete(UUID id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Endereco nao encontrado com id: " + id);
        }
        addressRepository.deleteById(id);
    }

    private Address findAddress(UUID id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + id));
    }
}
