package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;

public final class EmployeeDtoMapper {

    private EmployeeDtoMapper() {
    }

    public static Employee toDomain(EmployeeRequest request, Address address) {
        return Employee.create(
                request.name(),
                request.email(),
                request.phone(),
                address,
                request.role()
        );
    }

    public static void applyTo(Employee employee, EmployeeRequest request, Address address) {
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setRole(request.role());
        employee.setAddress(address);
    }

    public static EmployeeResponse toResponse(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                AddressDtoMapper.toResponse(employee.getAddress()),
                employee.getRole(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
