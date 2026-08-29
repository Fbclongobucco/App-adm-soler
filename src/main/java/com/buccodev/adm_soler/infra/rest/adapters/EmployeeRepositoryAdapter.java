package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.infra.rest.entities.EmployeeJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.EmployeeJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.EmployeeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class EmployeeRepositoryAdapter implements EmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;

    public EmployeeRepositoryAdapter(EmployeeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Transactional
    @Override
    public Employee save(Employee employee) {
        EmployeeJpa jpa = EmployeeMapper.toJpa(employee);
        if (jpaRepository.existsById(jpa.getId())) {
            jpa.markAsExisting();
        }
        EmployeeJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return EmployeeMapper.toDomain(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Employee> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return EmployeeMapper.toDomain(jpa);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Employee> findAll(PageQuery pageQuery) {
        Page<EmployeeJpa> page = jpaRepository.findAll(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(EmployeeMapper::toDomain).toList(),
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
