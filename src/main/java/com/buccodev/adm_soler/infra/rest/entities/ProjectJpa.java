package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class ProjectJpa implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String os;

    @Column(nullable = false)
    private String serviceProvided;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientJpa client;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<RestaurantJpa> restaurants = new HashSet<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<AccommodationJpa> accommodations = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_employees",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<EmployeeJpa> employees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_equipments",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private Set<EquipmentJpa> equipments = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity = true;

    public ProjectJpa() {
    }

    public ProjectJpa(UUID id, String os, String serviceProvided, ClientJpa client,
                      LocalDateTime startDate, LocalDateTime endDate,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.os = os;
        this.serviceProvided = serviceProvided;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Toda entidade que veio do banco existe, por definicao.
     *
     * Sem este callback o campo transiente {@code newEntity} continuava {@code true}
     * numa entidade recem-carregada, e o {@code delete} do Spring Data desiste em
     * silencio quando {@link #isNew()} responde {@code true}: o DELETE devolvia 204
     * sem apagar nada. Os adapters chamam {@code markAsExisting()} apos os seus
     * proprios findById, mas o {@code deleteById} faz uma busca interna que nao
     * passa por eles.
     */
    @PostLoad
    void markLoadedAsExisting() {
        this.newEntity = false;
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

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getServiceProvided() { return serviceProvided; }
    public void setServiceProvided(String serviceProvided) { this.serviceProvided = serviceProvided; }

    public ClientJpa getClient() { return client; }
    public void setClient(ClientJpa client) { this.client = client; }

    public Set<RestaurantJpa> getRestaurants() { return restaurants; }
    public void setRestaurants(Set<RestaurantJpa> restaurants) { this.restaurants = restaurants; }

    public Set<AccommodationJpa> getAccommodations() { return accommodations; }
    public void setAccommodations(Set<AccommodationJpa> accommodations) { this.accommodations = accommodations; }

    public Set<EmployeeJpa> getEmployees() { return employees; }
    public void setEmployees(Set<EmployeeJpa> employees) { this.employees = employees; }

    public Set<EquipmentJpa> getEquipments() { return equipments; }
    public void setEquipments(Set<EquipmentJpa> equipments) { this.equipments = equipments; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
