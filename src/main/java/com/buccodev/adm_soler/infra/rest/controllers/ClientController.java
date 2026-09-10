package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.client.ClientRequestDto;
import com.buccodev.adm_soler.application.dto.client.ClientResponseDto;
import com.buccodev.adm_soler.application.usecase.ClientUseCase;
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

@Tag(name = "Clients", description = "Clientes contratantes das obras")
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientUseCase clientUseCase;

    public ClientController(ClientUseCase clientUseCase) {
        this.clientUseCase = clientUseCase;
    }

    @Operation(summary = "Cria um cliente", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<ClientResponseDto> create(@Valid @RequestBody ClientRequestDto request) {
        ClientResponseDto created = clientUseCase.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um cliente por id")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(clientUseCase.getClientById(id));
    }

    @Operation(summary = "Lista clientes paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<ClientResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clientUseCase.listClients(page, size));
    }

    @Operation(summary = "Atualiza um cliente", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody ClientRequestDto request) {
        return ResponseEntity.ok(clientUseCase.updateClient(id, request));
    }

    @Operation(summary = "Remove um cliente", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clientUseCase.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
