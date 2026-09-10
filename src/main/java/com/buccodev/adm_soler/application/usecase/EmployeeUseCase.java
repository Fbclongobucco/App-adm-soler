package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.employee.EmployeeRequestDto;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponseDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.EmployeeNotFoundException;
import com.buccodev.adm_soler.application.mapper.EmployeeMapper;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
import com.buccodev.adm_soler.core.repository.Repository;

import java.util.UUID;

public class EmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;

    public EmployeeUseCase(EmployeeRepository employeeRepository, AddressRepository addressRepository) {
        this.employeeRepository = employeeRepository;
        this.addressRepository = addressRepository;
    }

    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        requireAddress(request.addressId());
        Employee saved = employeeRepository.save(EmployeeMapper.toDomain(request));
        return EmployeeMapper.toResponseDto(saved);
    }

    public EmployeeResponseDto getEmployeeById(UUID id) {
        return EmployeeMapper.toResponseDto(findEmployee(id));
    }

    public PageResponseDto<EmployeeResponseDto> listEmployees(int page, int size) {
        var result = employeeRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, EmployeeMapper::toResponseDto);
    }

    public EmployeeResponseDto updateEmployee(UUID id, EmployeeRequestDto request) {
        Employee employee = findEmployee(id);
        requireAddress(request.addressId());
        employee.update(request.name(), request.email(), request.phone(), request.addressId(),
                request.role());
        return EmployeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    public void deleteEmployee(UUID id) {
        employeeRepository.delete(findEmployee(id));
    }

    private Employee findEmployee(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> EmployeeNotFoundException.withId(id));
    }

    private void requireAddress(UUID addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw AddressNotFoundException.withId(addressId);
        }
    }
}
