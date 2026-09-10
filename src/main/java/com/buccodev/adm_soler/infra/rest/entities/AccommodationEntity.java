package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accommodations_tb")
public class AccommodationEntity {

    @Id
    private UUID id;

    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    private Integer capacity;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected AccommodationEntity() {
    }

    public AccommodationEntity(UUID id, UUID addressId, UUID projectId, Integer capacity, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.addressId = addressId;
        this.projectId = projectId;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AccommodationEntity that = (AccommodationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
