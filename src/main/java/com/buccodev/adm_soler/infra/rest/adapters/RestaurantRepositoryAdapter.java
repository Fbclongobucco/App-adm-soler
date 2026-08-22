package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import com.buccodev.adm_soler.infra.rest.entities.RestaurantJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.RestaurantJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.RestaurantMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    public RestaurantRepositoryAdapter(RestaurantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantJpa jpa = RestaurantMapper.toJpa(restaurant);
        RestaurantJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return RestaurantMapper.toDomain(saved);
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return RestaurantMapper.toDomain(jpa);
        });
    }

    @Override
    public List<Restaurant> findAll() {
        return jpaRepository.findAll().stream().map(RestaurantMapper::toDomain).toList();
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
