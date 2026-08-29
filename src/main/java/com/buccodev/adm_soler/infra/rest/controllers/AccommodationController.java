package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accommodations")
public class AccommodationController {

    private final AccommodationUseCase accommodationUseCase;

    public AccommodationController(AccommodationUseCase accommodationUseCase) {
        this.accommodationUseCase = accommodationUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationResponse> create(@RequestBody AccommodationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accommodationUseCase.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'FOREIGN')")
    public ResponseEntity<AccommodationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accommodationUseCase.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'FOREIGN')")
    public ResponseEntity<PageResponse<AccommodationResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(accommodationUseCase.findAll(page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationResponse> update(@PathVariable UUID id, @RequestBody AccommodationRequest request) {
        return ResponseEntity.ok(accommodationUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accommodationUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
