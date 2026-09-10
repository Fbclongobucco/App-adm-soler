package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.employee.EmployeeRequestDto;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponseDto;
import com.buccodev.adm_soler.core.domain.Employee;

public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static Employee toDomain(EmployeeRequestDto dto) {
        return Employee.create(dto.name(), dto.email(), dto.phone(), dto.addressId(), dto.role());
    }

    public static EmployeeResponseDto toResponseDto(Employee employee) {
        return new EmployeeResponseDto(employee.getId(), employee.getName(), employee.getEmail(),
                employee.getPhone(), employee.getAddressId(), employee.getRole(),
                employee.getCreatedAt(), employee.getUpdatedAt());
    }
}
