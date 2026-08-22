package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accommodations")
public class AccommodationController {

    private final AccommodationUseCase accommodationUseCase;

    public AccommodationController(AccommodationUseCase accommodationUseCase) {
        this.accommodationUseCase = accommodationUseCase;
    }

    @PostMapping
    public ResponseEntity<AccommodationResponse> create(@RequestBody AccommodationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accommodationUseCase.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accommodationUseCase.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<AccommodationResponse>> findAll() {
        return ResponseEntity.ok(accommodationUseCase.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccommodationResponse> update(@PathVariable UUID id, @RequestBody AccommodationRequest request) {
        return ResponseEntity.ok(accommodationUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accommodationUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
