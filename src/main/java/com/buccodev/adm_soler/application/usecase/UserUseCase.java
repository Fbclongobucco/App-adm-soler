package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.user.UserRequestDto;
import com.buccodev.adm_soler.application.dto.user.UserResponseDto;
import com.buccodev.adm_soler.application.exception.UserAlreadyExistsException;
import com.buccodev.adm_soler.application.exception.UserNotFoundException;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.application.mapper.UserMapper;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;

import java.util.UUID;

public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw UserAlreadyExistsException.withEmail(request.email());
        }
        User saved = userRepository.save(UserMapper.toDomain(withHashedPassword(request)));
        return UserMapper.toResponseDto(saved);
    }

    public UserResponseDto getUserById(UUID id) {
        return UserMapper.toResponseDto(findUser(id));
    }

    public PageResponseDto<UserResponseDto> listUsers(int page, int size) {
        var result = userRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, UserMapper::toResponseDto);
    }

    public UserResponseDto updateUser(UUID id, UserRequestDto request) {
        User user = findUser(id);
        user.update(request.name(), request.email(), passwordHasher.hash(request.password()),
                request.phone());
        return UserMapper.toResponseDto(userRepository.save(user));
    }

    public void deleteUser(UUID id) {
        userRepository.delete(findUser(id));
    }

    private UserRequestDto withHashedPassword(UserRequestDto request) {
        return new UserRequestDto(request.name(), request.email(),
                passwordHasher.hash(request.password()), request.phone());
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));
    }
}
