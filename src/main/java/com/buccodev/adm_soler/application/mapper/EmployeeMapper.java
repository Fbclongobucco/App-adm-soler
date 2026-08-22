package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;

public class EmployeeMapper {

    public static Employee toDomain(EmployeeRequest request, Address address) {
        return Employee.create(
                request.name(),
                request.email(),
                request.phone(),
                address,
                request.role()
        );
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress() != null ? AddressMapper.toResponse(employee.getAddress()) : null,
                employee.getRole(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
