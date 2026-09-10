package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.address.AddressRequestDto;
import com.buccodev.adm_soler.application.dto.address.AddressResponseDto;
import com.buccodev.adm_soler.application.usecase.AddressUseCase;
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

@Tag(name = "Addresses", description = "Enderecos usados por clientes, funcionarios, restaurantes e acomodacoes")
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressUseCase addressUseCase;

    public AddressController(AddressUseCase addressUseCase) {
        this.addressUseCase = addressUseCase;
    }

    @Operation(summary = "Cria um endereco", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<AddressResponseDto> create(@Valid @RequestBody AddressRequestDto request) {
        AddressResponseDto created = addressUseCase.createAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um endereco por id")
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(addressUseCase.getAddressById(id));
    }

    @Operation(summary = "Lista enderecos paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<AddressResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(addressUseCase.listAddresses(page, size));
    }

    @Operation(summary = "Atualiza um endereco", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody AddressRequestDto request) {
        return ResponseEntity.ok(addressUseCase.updateAddress(id, request));
    }

    @Operation(summary = "Remove um endereco", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        addressUseCase.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
