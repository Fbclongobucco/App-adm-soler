package com.buccodev.adm_soler.application.dto.employee;

import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;

import java.util.UUID;

public record EmployeeRequest(
        String name,
        String email,
        String phone,
        UUID addressId,
        String role
) {
    public Employee toDomain(Address address) {
        return Employee.create(
                name,
                email,
                phone,
                address,
                role
        );
    }
}
