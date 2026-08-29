package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.infra.rest.entities.EquipmentJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.EquipmentJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.EquipmentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class EquipmentRepositoryAdapter implements EquipmentRepository {

    private final EquipmentJpaRepository jpaRepository;

    public EquipmentRepositoryAdapter(EquipmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
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

    @Transactional(readOnly = true)
    @Override
    public Optional<Equipment> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return EquipmentMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Equipment> findAll(PageQuery pageQuery) {
        Page<EquipmentJpa> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(EquipmentMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
