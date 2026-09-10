package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequestDto;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponseDto;
import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
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

@Tag(name = "Accommodations", description = "Alojamentos vinculados a uma obra")
@RestController
@RequestMapping("/api/v1/accommodations")
public class AccommodationController {

    private final AccommodationUseCase accommodationUseCase;

    public AccommodationController(AccommodationUseCase accommodationUseCase) {
        this.accommodationUseCase = accommodationUseCase;
    }

    @Operation(summary = "Cria uma acomodacao", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<AccommodationResponseDto> create(@Valid @RequestBody AccommodationRequestDto request) {
        AccommodationResponseDto created = accommodationUseCase.createAccommodation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca uma acomodacao por id")
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(accommodationUseCase.getAccommodationById(id));
    }

    @Operation(summary = "Lista acomodacoes paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<AccommodationResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(accommodationUseCase.listAccommodations(page, size));
    }

    @Operation(summary = "Atualiza uma acomodacao", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody AccommodationRequestDto request) {
        return ResponseEntity.ok(accommodationUseCase.updateAccommodation(id, request));
    }

    @Operation(summary = "Remove uma acomodacao", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accommodationUseCase.deleteAccommodation(id);
        return ResponseEntity.noContent().build();
    }
}
