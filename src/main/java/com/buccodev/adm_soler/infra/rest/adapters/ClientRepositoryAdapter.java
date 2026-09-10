package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.infra.rest.entities.ClientEntity;
import com.buccodev.adm_soler.infra.rest.jpa_repository.ClientJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client client) {
        return toDomain(jpaRepository.save(toEntity(client)));
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(ClientRepositoryAdapter::toDomain);
    }

    @Override
    public Repository.PageResult<Client> findAll(Repository.PageQuery pageQuery) {
        Page<ClientEntity> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new Repository.PageResult<>(
                page.getContent().stream().map(ClientRepositoryAdapter::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public void delete(Client client) {
        jpaRepository.deleteById(client.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    static ClientEntity toEntity(Client client) {
        return new ClientEntity(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCnpj(),
                client.getAddressId(),
                client.getCreatedAt(),
                client.getUpdatedAt());
    }

    static Client toDomain(ClientEntity entity) {
        return Client.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getCnpj(),
                entity.getAddressId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
