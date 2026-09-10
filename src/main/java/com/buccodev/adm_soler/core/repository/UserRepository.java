package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends Repository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
