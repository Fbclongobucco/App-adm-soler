package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.user.UserRequestDto;
import com.buccodev.adm_soler.application.dto.user.UserResponseDto;
import com.buccodev.adm_soler.application.usecase.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Users", description = "Usuarios do sistema e seus papeis")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @Operation(summary = "Cria um usuario", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto request) {
        UserResponseDto created = userUseCase.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um usuario por id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userUseCase.getUserById(id));
    }

    @Operation(summary = "Lista usuarios paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userUseCase.listUsers(page, size));
    }

    @Operation(summary = "Atualiza um usuario", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userUseCase.updateUser(id, request));
    }

    @Operation(summary = "Remove um usuario", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
