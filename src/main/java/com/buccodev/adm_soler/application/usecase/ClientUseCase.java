package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.ClientMapper;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;

import java.util.List;
import java.util.UUID;

public class ClientUseCase {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;

    public ClientUseCase(ClientRepository clientRepository, AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    public ClientResponse create(ClientRequest request) {
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var client = ClientMapper.toDomain(request, address);
        return ClientMapper.toResponse(clientRepository.save(client));
    }

    public ClientResponse findById(UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return ClientMapper.toResponse(client);
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toResponse)
                .toList();
    }

    public ClientResponse update(UUID id, ClientRequest request) {
        findById(id);
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var client = ClientMapper.toDomain(request, address);
        return ClientMapper.toResponse(clientRepository.save(client));
    }

    public void delete(UUID id) {
        findById(id);
        clientRepository.deleteById(id);
    }
}
