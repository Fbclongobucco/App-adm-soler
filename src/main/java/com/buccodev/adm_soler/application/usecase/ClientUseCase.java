package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
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
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        Client client = request.toDomain(address);
        Client saved = clientRepository.save(client);
        return ClientResponse.fromDomain(saved);
    }

    public ClientResponse findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + id));
        return ClientResponse.fromDomain(client);
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(ClientResponse::fromDomain)
                .toList();
    }

    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + id));
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setCnpj(request.cnpj());
        client.setAddress(address);
        Client updated = clientRepository.save(client);
        return ClientResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente nao encontrado com id: " + id);
        }
        clientRepository.deleteById(id);
    }
}
