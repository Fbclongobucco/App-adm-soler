package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    PageResult<User> findAll(PageQuery pageQuery);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
