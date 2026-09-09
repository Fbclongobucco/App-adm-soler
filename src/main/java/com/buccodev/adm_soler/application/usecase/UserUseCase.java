package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.gateway.PasswordEncoderPort;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.application.mapper.UserDtoMapper;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.UserRepository;

import java.util.UUID;

public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UserUseCase(UserRepository userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(UserRequest request) {
        User user = UserDtoMapper.toDomain(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        return UserDtoMapper.toResponse(userRepository.save(user));
    }

    public UserResponse findById(UUID id) {
        return UserDtoMapper.toResponse(findUser(id));
    }

    public PageResponse<UserResponse> findAll(int page, int size) {
        PageResult<User> result = userRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, UserDtoMapper::toResponse);
    }

    public UserResponse update(UUID id, UserRequest request) {
        User user = findUser(id);
        UserDtoMapper.applyTo(user, request);
        user.setPassword(passwordEncoder.encode(request.password()));
        return UserDtoMapper.toResponse(userRepository.save(user));
    }

    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario nao encontrado com id: " + id);
        }
        userRepository.deleteById(id);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + id));
    }
}
