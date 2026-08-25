package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.user.UserRequest;
import com.buccodev.adm_soler.application.dto.user.UserResponse;
import com.buccodev.adm_soler.application.dto.user.ChangeRoleRequest;
import com.buccodev.adm_soler.application.usecase.UserUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userUseCase.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userUseCase.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userUseCase.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userUseCase.update(id, request));
    }

    /**
     * Troca de perfil. Operacao critica: alem da regra de rota (ADMIN), a
     * anotacao garante a checagem mesmo que a cadeia de filtros mude.
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeRole(@PathVariable UUID id,
                                                   @RequestBody ChangeRoleRequest request) {
        return ResponseEntity.ok(userUseCase.changeRole(id, request.role()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
