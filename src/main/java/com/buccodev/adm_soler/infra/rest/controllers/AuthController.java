package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.auth.AuthResponse;
import com.buccodev.adm_soler.application.dto.auth.ChangePasswordRequest;
import com.buccodev.adm_soler.application.dto.auth.LoginRequest;
import com.buccodev.adm_soler.application.dto.auth.RefreshRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.usecase.AuthUseCase;
import com.buccodev.adm_soler.infra.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    /** Publico. Troca email e senha por um par access token / refresh token. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }

    /**
     * Publico (o proprio refresh token e a credencial). Rotaciona a sessao:
     * o token enviado deixa de valer e um novo par e devolvido.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authUseCase.refresh(request.refreshToken()));
    }

    /** Encerra a sessao apresentada. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        authUseCase.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /** Encerra todas as sessoes do usuario autenticado. */
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal AuthenticatedUser principal) {
        authUseCase.logoutAll(principal.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(authUseCase.me(principal.id()));
    }

    /** Troca da propria senha; derruba as sessoes abertas. */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changeOwnPassword(@AuthenticationPrincipal AuthenticatedUser principal,
                                                  @RequestBody ChangePasswordRequest request) {
        authUseCase.changeOwnPassword(principal.id(), request);
        return ResponseEntity.noContent().build();
    }
}
