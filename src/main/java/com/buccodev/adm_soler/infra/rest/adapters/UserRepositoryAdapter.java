package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.infra.rest.entities.UserJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.UserJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public User save(User user) {
        UserJpa jpa = UserMapper.toJpa(user);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        UserJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return UserMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return UserMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(UserMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(jpa -> {
            jpa.markAsExisting();
            return UserMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
