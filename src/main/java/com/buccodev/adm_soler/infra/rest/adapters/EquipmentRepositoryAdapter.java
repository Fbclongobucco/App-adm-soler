package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.infra.rest.entities.EquipmentEntity;
import com.buccodev.adm_soler.infra.rest.jpa_repository.EquipmentJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

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
        return toDomain(jpaRepository.save(toEntity(equipment)));
    }

    @Override
    public Optional<Equipment> findById(UUID id) {
        return jpaRepository.findById(id).map(EquipmentRepositoryAdapter::toDomain);
    }

    @Override
    public Repository.PageResult<Equipment> findAll(Repository.PageQuery pageQuery) {
        Page<EquipmentEntity> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new Repository.PageResult<>(
                page.getContent().stream().map(EquipmentRepositoryAdapter::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public void delete(Equipment equipment) {
        jpaRepository.deleteById(equipment.getId());
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    static EquipmentEntity toEntity(Equipment equipment) {
        return new EquipmentEntity(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt());
    }

    static Equipment toDomain(EquipmentEntity entity) {
        return Equipment.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
