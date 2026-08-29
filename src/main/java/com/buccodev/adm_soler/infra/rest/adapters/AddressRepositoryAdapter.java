package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.infra.rest.entities.AddressJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.AddressJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.AddressMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class AddressRepositoryAdapter implements AddressRepository {

    private final AddressJpaRepository jpaRepository;

    public AddressRepositoryAdapter(AddressJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public Address save(Address address) {
        AddressJpa jpa = AddressMapper.toJpa(address);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        AddressJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return AddressMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Address> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return AddressMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Address> findAll(PageQuery pageQuery) {
        Page<AddressJpa> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(AddressMapper::toDomain).toList(),
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
