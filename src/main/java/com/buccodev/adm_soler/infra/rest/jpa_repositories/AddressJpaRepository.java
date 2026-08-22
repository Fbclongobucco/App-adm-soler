package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.AddressJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressJpaRepository extends JpaRepository<AddressJpa, UUID> {

    List<AddressJpa> findByCity(String city);

    List<AddressJpa> findByState(String state);

    List<AddressJpa> findByZipCode(String zipCode);

    List<AddressJpa> findByCityAndState(String city, String state);

    Optional<AddressJpa> findByStreetAndNumberAndCity(String street, String number, String city);
}
