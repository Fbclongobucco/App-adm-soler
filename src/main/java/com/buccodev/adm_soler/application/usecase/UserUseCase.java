package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.UserMapper;
import com.buccodev.adm_soler.core.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserUseCase {

    private final UserRepository userRepository;

    public UserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UserRequest request) {
        var user = UserMapper.toDomain(request);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public UserResponse findById(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse update(UUID id, UserRequest request) {
        findById(id);
        var user = UserMapper.toDomain(request);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public void delete(UUID id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
