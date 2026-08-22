package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(UUID id);
    List<Client> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
