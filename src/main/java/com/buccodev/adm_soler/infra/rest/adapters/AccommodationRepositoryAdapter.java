package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.infra.rest.entities.AccommodationEntity;
import com.buccodev.adm_soler.infra.rest.jpa_repository.AccommodationJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AccommodationRepositoryAdapter implements AccommodationRepository {

    private final AccommodationJpaRepository jpaRepository;

    public AccommodationRepositoryAdapter(AccommodationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Accommodation save(Accommodation accommodation) {
        return toDomain(jpaRepository.save(toEntity(accommodation)));
    }

    @Override
    public Optional<Accommodation> findById(UUID id) {
        return jpaRepository.findById(id).map(AccommodationRepositoryAdapter::toDomain);
    }

    @Override
    public Repository.PageResult<Accommodation> findAll(Repository.PageQuery pageQuery) {
        Page<AccommodationEntity> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new Repository.PageResult<>(
                page.getContent().stream().map(AccommodationRepositoryAdapter::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public void delete(Accommodation accommodation) {
        jpaRepository.deleteById(accommodation.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    static AccommodationEntity toEntity(Accommodation accommodation) {
        return new AccommodationEntity(
                accommodation.getId(),
                accommodation.getAddressId(),
                accommodation.getProjectId(),
                accommodation.getCapacity(),
                accommodation.getStartDate(),
                accommodation.getEndDate(),
                accommodation.getCreatedAt(),
                accommodation.getUpdatedAt());
    }

    static Accommodation toDomain(AccommodationEntity entity) {
        return Accommodation.restore(
                entity.getId(),
                entity.getAddressId(),
                entity.getProjectId(),
                entity.getCapacity(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
