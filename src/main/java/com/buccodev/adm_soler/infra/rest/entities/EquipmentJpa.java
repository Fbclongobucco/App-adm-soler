package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipment")
public class EquipmentJpa implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity = true;

    public EquipmentJpa() {
    }

    public EquipmentJpa(UUID id, String name, String description,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
