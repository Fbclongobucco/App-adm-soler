package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.auth.LoginRequestDto;
import com.buccodev.adm_soler.application.dto.auth.RefreshRequestDto;
import com.buccodev.adm_soler.application.dto.auth.TokenResponseDto;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Login e renovacao de token — nao requer autenticacao")
@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @Operation(summary = "Autentica por email e senha",
            description = "Devolve um access token de vida curta e um refresh token de vida longa.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }

    @Operation(summary = "Troca um refresh token por um novo access token",
            description = "O refresh token nao e rotacionado e volta inalterado. "
                    + "Um access token nao e aceito aqui.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@Valid @RequestBody RefreshRequestDto request) {
        return ResponseEntity.ok(authUseCase.refresh(request.refreshToken()));
    }
}
