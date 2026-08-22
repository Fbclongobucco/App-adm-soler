package com.buccodev.adm_soler.application.dto.employee;

import com.buccodev.adm_soler.application.dto.address.AddressResponse;

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
}
