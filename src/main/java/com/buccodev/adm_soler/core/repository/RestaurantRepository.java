package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Restaurant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);
    Optional<Restaurant> findById(UUID id);
    List<Restaurant> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
