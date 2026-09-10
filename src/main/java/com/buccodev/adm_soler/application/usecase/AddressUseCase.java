package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.address.AddressRequestDto;
import com.buccodev.adm_soler.application.dto.address.AddressResponseDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.mapper.AddressMapper;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.UUID;

public class AddressUseCase {

    private final AddressRepository addressRepository;

    public AddressUseCase(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public AddressResponseDto createAddress(AddressRequestDto request) {
        Address saved = addressRepository.save(AddressMapper.toDomain(request));
        return AddressMapper.toResponseDto(saved);
    }

    public AddressResponseDto getAddressById(UUID id) {
        return AddressMapper.toResponseDto(findAddress(id));
    }

    public PageResponseDto<AddressResponseDto> listAddresses(int page, int size) {
        var result = addressRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, AddressMapper::toResponseDto);
    }

    public AddressResponseDto updateAddress(UUID id, AddressRequestDto request) {
        Address address = findAddress(id);
        address.update(request.street(), request.number(), request.complement(),
                request.neighborhood(), request.city(), request.state(), request.zipCode(),
                request.country());
        return AddressMapper.toResponseDto(addressRepository.save(address));
    }

    public void deleteAddress(UUID id) {
        addressRepository.delete(findAddress(id));
    }

    private Address findAddress(UUID id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> AddressNotFoundException.withId(id));
    }
}
