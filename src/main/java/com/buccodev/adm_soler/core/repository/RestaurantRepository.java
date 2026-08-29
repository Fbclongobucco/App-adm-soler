package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Restaurant;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);
    Optional<Restaurant> findById(UUID id);
    PageResult<Restaurant> findAll(PageQuery pageQuery);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
