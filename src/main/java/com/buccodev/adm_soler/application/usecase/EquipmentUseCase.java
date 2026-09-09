package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.EquipmentDtoMapper;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;

import java.util.UUID;

public class EquipmentUseCase {

    private final EquipmentRepository equipmentRepository;

    public EquipmentUseCase(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public EquipmentResponse create(EquipmentRequest request) {
        Equipment saved = equipmentRepository.save(EquipmentDtoMapper.toDomain(request));
        return EquipmentDtoMapper.toResponse(saved);
    }

    public EquipmentResponse findById(UUID id) {
        return EquipmentDtoMapper.toResponse(findEquipment(id));
    }

    public PageResponse<EquipmentResponse> findAll(int page, int size) {
        PageResult<Equipment> result = equipmentRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, EquipmentDtoMapper::toResponse);
    }

    public EquipmentResponse update(UUID id, EquipmentRequest request) {
        Equipment equipment = findEquipment(id);
        EquipmentDtoMapper.applyTo(equipment, request);
        return EquipmentDtoMapper.toResponse(equipmentRepository.save(equipment));
    }

    public void delete(UUID id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipamento nao encontrado com id: " + id);
        }
        equipmentRepository.deleteById(id);
    }

    private Equipment findEquipment(UUID id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento nao encontrado com id: " + id));
    }
}
