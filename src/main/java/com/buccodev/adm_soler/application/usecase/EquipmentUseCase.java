package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.EquipmentMapper;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;

import java.util.List;
import java.util.UUID;

public class EquipmentUseCase {

    private final EquipmentRepository equipmentRepository;

    public EquipmentUseCase(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public EquipmentResponse create(EquipmentRequest request) {
        var equipment = EquipmentMapper.toDomain(request);
        return EquipmentMapper.toResponse(equipmentRepository.save(equipment));
    }

    public EquipmentResponse findById(UUID id) {
        var equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        return EquipmentMapper.toResponse(equipment);
    }

    public List<EquipmentResponse> findAll() {
        return equipmentRepository.findAll().stream()
                .map(EquipmentMapper::toResponse)
                .toList();
    }

    public EquipmentResponse update(UUID id, EquipmentRequest request) {
        findById(id);
        var equipment = EquipmentMapper.toDomain(request);
        return EquipmentMapper.toResponse(equipmentRepository.save(equipment));
    }

    public void delete(UUID id) {
        findById(id);
        equipmentRepository.deleteById(id);
    }
}
