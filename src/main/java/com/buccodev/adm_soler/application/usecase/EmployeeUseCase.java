package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.EmployeeDtoMapper;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;

import java.util.UUID;

public class EmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;

    public EmployeeUseCase(EmployeeRepository employeeRepository, AddressRepository addressRepository) {
        this.employeeRepository = employeeRepository;
        this.addressRepository = addressRepository;
    }

    public EmployeeResponse create(EmployeeRequest request) {
        Address address = findAddress(request.addressId());
        Employee saved = employeeRepository.save(EmployeeDtoMapper.toDomain(request, address));
        return EmployeeDtoMapper.toResponse(saved);
    }

    public EmployeeResponse findById(UUID id) {
        return EmployeeDtoMapper.toResponse(findEmployee(id));
    }

    public PageResponse<EmployeeResponse> findAll(int page, int size) {
        PageResult<Employee> result = employeeRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, EmployeeDtoMapper::toResponse);
    }

    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        Address address = findAddress(request.addressId());
        EmployeeDtoMapper.applyTo(employee, request, address);
        return EmployeeDtoMapper.toResponse(employeeRepository.save(employee));
    }

    public void delete(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Funcionario nao encontrado com id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee findEmployee(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado com id: " + id));
    }

    private Address findAddress(UUID addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + addressId));
    }
}
