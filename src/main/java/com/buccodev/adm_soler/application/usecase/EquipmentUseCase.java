package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EquipmentUseCase {

    private final EquipmentRepository equipmentRepository;

    public EquipmentUseCase(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public EquipmentResponse create(EquipmentRequest request) {
        Equipment equipment = request.toDomain();
        Equipment saved = equipmentRepository.save(equipment);
        return EquipmentResponse.fromDomain(saved);
    }

    public EquipmentResponse findById(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento nao encontrado com id: " + id));
        return EquipmentResponse.fromDomain(equipment);
    }

    public List<EquipmentResponse> findAll() {
        return equipmentRepository.findAll().stream()
                .map(EquipmentResponse::fromDomain)
                .toList();
    }

    public EquipmentResponse update(UUID id, EquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento nao encontrado com id: " + id));
        equipment.setName(request.name());
        equipment.setDescription(request.description());
        Equipment updated = equipmentRepository.save(equipment);
        return EquipmentResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipamento nao encontrado com id: " + id);
        }
        equipmentRepository.deleteById(id);
    }
}
