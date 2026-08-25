package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.application.usecase.EquipmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipments")
public class EquipmentController {

    private final EquipmentUseCase equipmentUseCase;

    public EquipmentController(EquipmentUseCase equipmentUseCase) {
        this.equipmentUseCase = equipmentUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> create(@RequestBody EquipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentUseCase.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'FOREIGN')")
    public ResponseEntity<EquipmentResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(equipmentUseCase.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'FOREIGN')")
    public ResponseEntity<List<EquipmentResponse>> findAll() {
        return ResponseEntity.ok(equipmentUseCase.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> update(@PathVariable UUID id, @RequestBody EquipmentRequest request) {
        return ResponseEntity.ok(equipmentUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        equipmentUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
