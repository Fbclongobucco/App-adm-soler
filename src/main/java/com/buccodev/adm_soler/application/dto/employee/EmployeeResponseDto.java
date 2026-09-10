package com.buccodev.adm_soler.application.dto.employee;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponseDto(UUID id, String name, String email, String phone, UUID addressId,
                                  String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
