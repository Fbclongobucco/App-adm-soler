package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;

    public EmployeeUseCase(EmployeeRepository employeeRepository, AddressRepository addressRepository) {
        this.employeeRepository = employeeRepository;
        this.addressRepository = addressRepository;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        Employee employee = request.toDomain(address);
        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromDomain(saved);
    }

    public EmployeeResponse findById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado com id: " + id));
        return EmployeeResponse.fromDomain(employee);
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromDomain)
                .toList();
    }

    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado com id: " + id));
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setRole(request.role());
        employee.setAddress(address);
        Employee updated = employeeRepository.save(employee);
        return EmployeeResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Funcionario nao encontrado com id: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
