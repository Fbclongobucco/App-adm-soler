package com.buccodev.adm_soler.infra.rest.adapters;

import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import com.buccodev.adm_soler.infra.rest.entities.EmployeeJpa;
import com.buccodev.adm_soler.infra.rest.jpa_repositories.EmployeeJpaRepository;
import com.buccodev.adm_soler.infra.rest.mappers.EmployeeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmployeeRepositoryAdapter implements EmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;

    public EmployeeRepositoryAdapter(EmployeeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeJpa jpa = EmployeeMapper.toJpa(employee);
        EmployeeJpa saved = jpaRepository.save(jpa);
        saved.markAsExisting();
        return EmployeeMapper.toDomain(saved);
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return jpaRepository.findById(id).map(jpa -> {
            jpa.markAsExisting();
            return EmployeeMapper.toDomain(jpa);
        });
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAll().stream().map(EmployeeMapper::toDomain).toList();
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
