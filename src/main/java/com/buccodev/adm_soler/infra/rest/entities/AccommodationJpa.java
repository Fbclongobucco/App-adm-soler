package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accommodations")
public class AccommodationJpa implements Persistable<UUID> {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressJpa address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectJpa project;

    private Integer capacity;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "accommodation_employees",
        joinColumns = @JoinColumn(name = "accommodation_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<EmployeeJpa> employees = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity = true;

    public AccommodationJpa() {
    }

    public AccommodationJpa(UUID id, AddressJpa address, ProjectJpa project, Integer capacity,
                            LocalDateTime startDate, LocalDateTime endDate,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.address = address;
        this.project = project;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    public void markAsExisting() {
        this.newEntity = false;
    }

    @Override
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public AddressJpa getAddress() { return address; }
    public void setAddress(AddressJpa address) { this.address = address; }

    public ProjectJpa getProject() { return project; }
    public void setProject(ProjectJpa project) { this.project = project; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Set<EmployeeJpa> getEmployees() { return employees; }
    public void setEmployees(Set<EmployeeJpa> employees) { this.employees = employees; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
