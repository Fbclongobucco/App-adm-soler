package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequestDto;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponseDto;
import com.buccodev.adm_soler.application.usecase.EquipmentUseCase;
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

@Tag(name = "Equipments", description = "Equipamentos disponiveis para as obras")
@RestController
@RequestMapping("/api/v1/equipments")
public class EquipmentController {

    private final EquipmentUseCase equipmentUseCase;

    public EquipmentController(EquipmentUseCase equipmentUseCase) {
        this.equipmentUseCase = equipmentUseCase;
    }

    @Operation(summary = "Cria um equipamento", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<EquipmentResponseDto> create(@Valid @RequestBody EquipmentRequestDto request) {
        EquipmentResponseDto created = equipmentUseCase.createEquipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um equipamento por id")
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(equipmentUseCase.getEquipmentById(id));
    }

    @Operation(summary = "Lista equipamentos paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<EquipmentResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(equipmentUseCase.listEquipments(page, size));
    }

    @Operation(summary = "Atualiza um equipamento", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody EquipmentRequestDto request) {
        return ResponseEntity.ok(equipmentUseCase.updateEquipment(id, request));
    }

    @Operation(summary = "Remove um equipamento", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        equipmentUseCase.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
