package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository {
    Address save(Address address);
    Optional<Address> findById(UUID id);
    List<Address> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
