package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.infra.rest.entities.AccommodationJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.AccommodationJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.AccommodationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class AccommodationRepositoryAdapter implements AccommodationRepository {

    private final AccommodationJpaRepository jpaRepository;

    public AccommodationRepositoryAdapter(AccommodationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public Accommodation save(Accommodation accommodation) {
        AccommodationJpa jpa = AccommodationMapper.toJpa(accommodation);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        AccommodationJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return AccommodationMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Accommodation> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return AccommodationMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Accommodation> findAll(PageQuery pageQuery) {
        Page<AccommodationJpa> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(AccommodationMapper::toDomain).toList(),
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
