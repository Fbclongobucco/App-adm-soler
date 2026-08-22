package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.infra.rest.entities.EquipmentJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.EquipmentJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.EquipmentMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EquipmentRepositoryAdapter implements EquipmentRepository {

    private final EquipmentJpaRepository jpaRepository;

    public EquipmentRepositoryAdapter(EquipmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Equipment save(Equipment equipment) {
        EquipmentJpa jpa = EquipmentMapper.toJpa(equipment);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        EquipmentJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return EquipmentMapper.toDomain(saved);
    }

    @Override
    public Optional<Equipment> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return EquipmentMapper.toDomain(jpa);
        });
    }

    @Override
    public List<Equipment> findAll() {
        return jpaRepository.findAll().stream().map(EquipmentMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
