package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.exception.ConflictException;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordHasher;

    public UserUseCase(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordHasher = passwordHasher;
    }

    public UserResponse create(UserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Ja existe um usuario com o email: " + email);
        }

        // User.create valida a senha em claro; so depois ela vira hash.
        User user = User.create(request.name(), email, request.password(), request.phone(),
                request.roleOrDefault());
        user.applyHashedPassword(passwordHasher.hash(request.password()));

        return UserResponse.fromDomain(userRepository.save(user));
    }

    public UserResponse findById(UUID id) {
        return UserResponse.fromDomain(findUserOrFail(id));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromDomain)
                .toList();
    }

    public UserResponse update(UUID id, UserRequest request) {
        User user = findUserOrFail(id);

        String email = normalizeEmail(request.email());
        if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new ConflictException("Ja existe um usuario com o email: " + email);
        }

        user.setName(request.name());
        user.setEmail(email);
        user.setPhone(request.phone());

        // Senha em branco no update significa "manter a atual".
        if (request.password() != null && !request.password().isBlank()) {
            User.requireStrongPassword(request.password());
            user.applyHashedPassword(passwordHasher.hash(request.password()));
            revokeSessions(id);
        }

        user.setUpdatedAt(LocalDateTime.now());
        return UserResponse.fromDomain(userRepository.save(user));
    }

    /**
     * Troca de perfil. Operacao critica: o perfil viaja dentro do access token,
     * entao as sessoes abertas sao revogadas para o novo perfil valer de fato
     * assim que o access token corrente expirar.
     */
    public UserResponse changeRole(UUID id, User.Role role) {
        if (role == null) {
            throw new BadRequestException("role e obrigatorio");
        }
        User user = findUserOrFail(id);
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        User updated = userRepository.save(user);
        revokeSessions(id);
        return UserResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario nao encontrado com id: " + id);
        }
        refreshTokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private User findUserOrFail(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + id));
    }

    private void revokeSessions(UUID userId) {
        for (RefreshToken token : refreshTokenRepository.findActiveByUserId(userId)) {
            token.revoke(RefreshToken.RevokedReason.SECURITY);
            refreshTokenRepository.save(token);
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("email e obrigatorio");
        }
        return email.trim().toLowerCase();
    }
}
