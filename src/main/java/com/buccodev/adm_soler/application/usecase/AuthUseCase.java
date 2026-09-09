package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.auth.AuthRequest;
import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.RefreshTokenRequest;
import com.buccodev.adm_soler.application.dto.auth.RegisterRequest;
import com.buccodev.adm_soler.application.exception.AuthenticationException;
import com.buccodev.adm_soler.application.exception.BadRequestException;
import com.buccodev.adm_soler.application.gateway.AuthenticationGatewayPort;
import com.buccodev.adm_soler.application.gateway.PasswordEncoderPort;
import com.buccodev.adm_soler.application.gateway.TokenProviderPort;
import com.buccodev.adm_soler.application.mapper.AuthDtoMapper;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;

public class AuthUseCase {

    private final AuthenticationGatewayPort authenticationGateway;
    private final TokenProviderPort tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public AuthUseCase(AuthenticationGatewayPort authenticationGateway,
                       TokenProviderPort tokenProvider,
                       UserRepository userRepository,
                       PasswordEncoderPort passwordEncoder) {
        this.authenticationGateway = authenticationGateway;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationGateway.authenticate(request.email(), request.password());
        return issueTokens(findUserByEmail(request.email()));
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String email = tokenProvider.extractSubject(refreshToken);

        if (email == null) {
            throw new AuthenticationException("Invalid refresh token");
        }
        if (!tokenProvider.isTokenValid(refreshToken, email)) {
            throw new AuthenticationException("Invalid or expired refresh token");
        }

        return issueTokens(findUserByEmail(email));
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use: " + request.email());
        }

        User user = AuthDtoMapper.toDomain(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        return issueTokens(userRepository.save(user));
    }

    private AuthResponse issueTokens(User user) {
        return AuthDtoMapper.toResponse(
                user,
                tokenProvider.generateAccessToken(user.getEmail()),
                tokenProvider.generateRefreshToken(user.getEmail())
        );
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }
}
