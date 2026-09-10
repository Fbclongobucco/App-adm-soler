package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidAccommodationException;
import com.buccodev.adm_soler.core.exception.InvalidPeriodException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Accommodation {

    private final UUID id;
    private UUID addressId;
    private final UUID projectId;
    private Integer capacity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Accommodation(UUID id, UUID addressId, UUID projectId, Integer capacity,
                          LocalDateTime startDate, LocalDateTime endDate,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        validate(addressId, projectId, capacity, startDate, endDate);
        this.id = id;
        this.addressId = addressId;
        this.projectId = projectId;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Accommodation create(UUID addressId, UUID projectId, Integer capacity,
                                       LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        return new Accommodation(UUID.randomUUID(), addressId, projectId, capacity,
                startDate, endDate, now, now);
    }

    public static Accommodation restore(UUID id, UUID addressId, UUID projectId, Integer capacity,
                                        LocalDateTime startDate, LocalDateTime endDate,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Accommodation(id, addressId, projectId, capacity, startDate, endDate,
                createdAt, updatedAt);
    }

    /**
     * O projeto e fixo pela vida da acomodacao: mover uma acomodacao de obra
     * invalidaria a alocacao de quem ja esta hospedado nela.
     */
    public void update(UUID addressId, Integer capacity, LocalDateTime startDate, LocalDateTime endDate) {
        validate(addressId, projectId, capacity, startDate, endDate);
        this.addressId = addressId;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean fits(int employeeCount) {
        return capacity == null || employeeCount <= capacity;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    private static void validate(UUID addressId, UUID projectId, Integer capacity,
                                 LocalDateTime startDate, LocalDateTime endDate) {
        if (addressId == null) {
            throw InvalidAccommodationException.nullAddressId();
        }
        if (projectId == null) {
            throw InvalidAccommodationException.nullProjectId();
        }
        if (capacity != null && capacity <= 0) {
            throw InvalidAccommodationException.nonPositiveCapacity(capacity);
        }
        if (startDate == null || endDate == null) {
            throw InvalidPeriodException.nullBounds();
        }
        if (startDate.isAfter(endDate)) {
            throw InvalidPeriodException.startAfterEnd(startDate, endDate);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Accommodation that = (Accommodation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
