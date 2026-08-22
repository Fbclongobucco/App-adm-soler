package com.buccodev.adm_soler.application.dto.accommodation;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccommodationRequest(
        UUID addressId,
        UUID projectId,
        Integer capacity,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
