package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.ClientDtoMapper;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;

import java.util.UUID;

public class ClientUseCase {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;

    public ClientUseCase(ClientRepository clientRepository, AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    public ClientResponse create(ClientRequest request) {
        Address address = findAddress(request.addressId());
        Client saved = clientRepository.save(ClientDtoMapper.toDomain(request, address));
        return ClientDtoMapper.toResponse(saved);
    }

    public ClientResponse findById(UUID id) {
        return ClientDtoMapper.toResponse(findClient(id));
    }

    public PageResponse<ClientResponse> findAll(int page, int size) {
        PageResult<Client> result = clientRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, ClientDtoMapper::toResponse);
    }

    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = findClient(id);
        Address address = findAddress(request.addressId());
        ClientDtoMapper.applyTo(client, request, address);
        return ClientDtoMapper.toResponse(clientRepository.save(client));
    }

    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente nao encontrado com id: " + id);
        }
        clientRepository.deleteById(id);
    }

    private Client findClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado com id: " + id));
    }

    private Address findAddress(UUID addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + addressId));
    }
}
