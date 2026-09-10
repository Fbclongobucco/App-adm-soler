package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.auth.LoginRequestDto;
import com.buccodev.adm_soler.application.dto.auth.TokenResponseDto;
import com.buccodev.adm_soler.application.exception.InvalidCredentialsException;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import com.buccodev.adm_soler.core.security.TokenService;

public class AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public AuthUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public TokenResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .filter(candidate -> passwordHasher.matches(request.password(), candidate.getPassword()))
                .orElseThrow(InvalidCredentialsException::badLogin);
        return new TokenResponseDto(tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user));
    }

    public TokenResponseDto refresh(String refreshToken) {
        String email = tokenService.extractEmailIfValidRefreshToken(refreshToken)
                .orElseThrow(InvalidCredentialsException::badRefreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::badRefreshToken);
        return new TokenResponseDto(tokenService.generateAccessToken(user), refreshToken);
    }
}
