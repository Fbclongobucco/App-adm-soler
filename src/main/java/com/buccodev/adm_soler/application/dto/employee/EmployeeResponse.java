package com.buccodev.adm_soler.application.dto.employee;

import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.core.domain.Employee;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String name,
        String email,
        String phone,
        AddressResponse address,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EmployeeResponse fromDomain(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress() != null ? AddressResponse.fromDomain(employee.getAddress()) : null,
                employee.getRole(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
