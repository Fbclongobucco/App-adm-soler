package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.client.ClientRequestDto;
import com.buccodev.adm_soler.application.dto.client.ClientResponseDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ClientNotFoundException;
import com.buccodev.adm_soler.application.mapper.ClientMapper;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.UUID;

public class ClientUseCase {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;

    public ClientUseCase(ClientRepository clientRepository, AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    public ClientResponseDto createClient(ClientRequestDto request) {
        requireAddress(request.addressId());
        Client saved = clientRepository.save(ClientMapper.toDomain(request));
        return ClientMapper.toResponseDto(saved);
    }

    public ClientResponseDto getClientById(UUID id) {
        return ClientMapper.toResponseDto(findClient(id));
    }

    public PageResponseDto<ClientResponseDto> listClients(int page, int size) {
        var result = clientRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, ClientMapper::toResponseDto);
    }

    public ClientResponseDto updateClient(UUID id, ClientRequestDto request) {
        Client client = findClient(id);
        requireAddress(request.addressId());
        client.update(request.name(), request.email(), request.phone(), request.cnpj(),
                request.addressId());
        return ClientMapper.toResponseDto(clientRepository.save(client));
    }

    public void deleteClient(UUID id) {
        clientRepository.delete(findClient(id));
    }

    private Client findClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.withId(id));
    }

    private void requireAddress(UUID addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw AddressNotFoundException.withId(addressId);
        }
    }
}
