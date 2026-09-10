package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.infra.rest.entities.AddressEntity;
import com.buccodev.adm_soler.infra.rest.jpa_repository.AddressJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AddressRepositoryAdapter implements AddressRepository {

    private final AddressJpaRepository jpaRepository;

    public AddressRepositoryAdapter(AddressJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Address save(Address address) {
        return toDomain(jpaRepository.save(toEntity(address)));
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return jpaRepository.findById(id).map(AddressRepositoryAdapter::toDomain);
    }

    @Override
    public Repository.PageResult<Address> findAll(Repository.PageQuery pageQuery) {
        Page<AddressEntity> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new Repository.PageResult<>(
                page.getContent().stream().map(AddressRepositoryAdapter::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public void delete(Address address) {
        jpaRepository.deleteById(address.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    static AddressEntity toEntity(Address address) {
        return new AddressEntity(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.getCreatedAt(),
                address.getUpdatedAt());
    }

    static Address toDomain(AddressEntity entity) {
        return Address.restore(
                entity.getId(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode(),
                entity.getCountry(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
