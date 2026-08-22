package com.buccodev.adm_soler.application.dto.employee;

import java.util.UUID;

public record EmployeeRequest(
        String name,
        String email,
        String phone,
        UUID addressId,
        String role
) {
}
