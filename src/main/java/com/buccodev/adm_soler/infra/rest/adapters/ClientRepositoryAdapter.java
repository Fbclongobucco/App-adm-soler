package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.infra.rest.entities.ClientJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.ClientJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.ClientMapper;
import org.springframework.stereotype.Component;

import java.util.List;
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
        ClientJpa jpa = ClientMapper.toJpa(client);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        ClientJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return ClientMapper.toDomain(saved);
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return ClientMapper.toDomain(jpa);
        });
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream().map(ClientMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
