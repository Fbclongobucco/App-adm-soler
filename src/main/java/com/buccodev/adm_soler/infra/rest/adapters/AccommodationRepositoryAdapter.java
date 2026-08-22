package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.infra.rest.entities.AccommodationJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.AccommodationJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.AccommodationMapper;
import org.springframework.stereotype.Component;

import java.util.List;
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
        AccommodationJpa jpa = AccommodationMapper.toJpa(accommodation);
        AccommodationJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return AccommodationMapper.toDomain(saved);
    }

    @Override
    public Optional<Accommodation> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return AccommodationMapper.toDomain(jpa);
        });
    }

    @Override
    public List<Accommodation> findAll() {
        return jpaRepository.findAll().stream().map(AccommodationMapper::toDomain).toList();
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
