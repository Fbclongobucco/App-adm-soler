package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Accommodation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccommodationRepository {
    Accommodation save(Accommodation accommodation);
    Optional<Accommodation> findById(UUID id);
    List<Accommodation> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
