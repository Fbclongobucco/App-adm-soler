package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.infra.rest.entities.ClientJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.ClientJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.ClientMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
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

    @Transactional(readOnly = true)
    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return ClientMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Client> findAll(PageQuery pageQuery) {
        Page<ClientJpa> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(ClientMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
