package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequestDto;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponseDto;
import com.buccodev.adm_soler.application.exception.EquipmentNotFoundException;
import com.buccodev.adm_soler.application.mapper.EquipmentMapper;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.UUID;

public class EquipmentUseCase {

    private final EquipmentRepository equipmentRepository;

    public EquipmentUseCase(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public EquipmentResponseDto createEquipment(EquipmentRequestDto request) {
        Equipment saved = equipmentRepository.save(EquipmentMapper.toDomain(request));
        return EquipmentMapper.toResponseDto(saved);
    }

    public EquipmentResponseDto getEquipmentById(UUID id) {
        return EquipmentMapper.toResponseDto(findEquipment(id));
    }

    public PageResponseDto<EquipmentResponseDto> listEquipments(int page, int size) {
        var result = equipmentRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, EquipmentMapper::toResponseDto);
    }

    public EquipmentResponseDto updateEquipment(UUID id, EquipmentRequestDto request) {
        Equipment equipment = findEquipment(id);
        equipment.update(request.name(), request.description());
        return EquipmentMapper.toResponseDto(equipmentRepository.save(equipment));
    }

    public void deleteEquipment(UUID id) {
        equipmentRepository.delete(findEquipment(id));
    }

    private Equipment findEquipment(UUID id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> EquipmentNotFoundException.withId(id));
    }
}
