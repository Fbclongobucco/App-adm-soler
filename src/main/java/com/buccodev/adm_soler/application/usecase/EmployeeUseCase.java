package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.EmployeeMapper;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;

import java.util.List;
import java.util.UUID;

public class EmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;

    public EmployeeUseCase(EmployeeRepository employeeRepository, AddressRepository addressRepository) {
        this.employeeRepository = employeeRepository;
        this.addressRepository = addressRepository;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var employee = EmployeeMapper.toDomain(request, address);
        return EmployeeMapper.toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse findById(UUID id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return EmployeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        findById(id);
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var employee = EmployeeMapper.toDomain(request, address);
        return EmployeeMapper.toResponse(employeeRepository.save(employee));
    }

    public void delete(UUID id) {
        findById(id);
        employeeRepository.deleteById(id);
    }
}
