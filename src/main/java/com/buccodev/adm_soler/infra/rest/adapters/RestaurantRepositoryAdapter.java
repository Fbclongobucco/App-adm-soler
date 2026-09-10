package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.infra.rest.entities.RestaurantEntity;
import com.buccodev.adm_soler.infra.rest.jpa_repository.RestaurantJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

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
        return toDomain(jpaRepository.save(toEntity(restaurant)));
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return jpaRepository.findById(id).map(RestaurantRepositoryAdapter::toDomain);
    }

    @Override
    public Repository.PageResult<Restaurant> findAll(Repository.PageQuery pageQuery) {
        Page<RestaurantEntity> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new Repository.PageResult<>(
                page.getContent().stream().map(RestaurantRepositoryAdapter::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public void delete(Restaurant restaurant) {
        jpaRepository.deleteById(restaurant.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    static RestaurantEntity toEntity(Restaurant restaurant) {
        return new RestaurantEntity(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getEmail(),
                restaurant.getPhone(),
                restaurant.getCnpj(),
                restaurant.getProjectId(),
                restaurant.getAddressId(),
                restaurant.getIsBilled(),
                restaurant.getLunchPrice(),
                restaurant.getDinnerPrice(),
                restaurant.getAdditionalValues(),
                restaurant.getDays(),
                restaurant.getTotal(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt());
    }

    static Restaurant toDomain(RestaurantEntity entity) {
        return Restaurant.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getCnpj(),
                entity.getProjectId(),
                entity.getAddressId(),
                entity.getIsBilled(),
                entity.getLunchPrice(),
                entity.getDinnerPrice(),
                entity.getAdditionalValues(),
                entity.getDays(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
