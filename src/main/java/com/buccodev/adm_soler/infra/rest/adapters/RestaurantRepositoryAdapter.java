package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.infra.rest.entities.RestaurantJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.RestaurantJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.RestaurantMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    public RestaurantRepositoryAdapter(RestaurantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantJpa jpa = RestaurantMapper.toJpa(restaurant);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        RestaurantJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return RestaurantMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Restaurant> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return RestaurantMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Restaurant> findAll(PageQuery pageQuery) {
        Page<RestaurantJpa> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(RestaurantMapper::toDomain).toList(),
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
