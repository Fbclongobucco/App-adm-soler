package com.buccodev.adm_soler.application.dto.project;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponseDto(UUID id, String os, String serviceProvided, UUID clientId,
                                 LocalDateTime startDate, LocalDateTime endDate,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
}
