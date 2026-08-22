package com.buccodev.adm_soler.core.repository;

import com.buccodev.adm_soler.core.domain.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(UUID id);
    List<Employee> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
