package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
import com.buccodev.adm_soler.infra.rest.requests.AuthHttpRequest;
import com.buccodev.adm_soler.infra.rest.requests.RefreshTokenHttpRequest;
import com.buccodev.adm_soler.infra.rest.requests.RegisterHttpRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthHttpRequest request) {
        return ResponseEntity.ok(authUseCase.login(request.toApplicationRequest()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenHttpRequest request) {
        return ResponseEntity.ok(authUseCase.refreshToken(request.toApplicationRequest()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterHttpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authUseCase.register(request.toApplicationRequest()));
    }
}
