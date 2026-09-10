package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidEquipmentException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Equipment {

    private final UUID id;
    private String name;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Equipment(UUID id, String name, String description, LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
        validate(name);
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Equipment create(String name, String description) {
        LocalDateTime now = LocalDateTime.now();
        return new Equipment(UUID.randomUUID(), name, description, now, now);
    }

    public static Equipment restore(UUID id, String name, String description,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Equipment(id, name, description, createdAt, updatedAt);
    }

    public void update(String name, String description) {
        validate(name);
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    private static void validate(String name) {
        if (name == null || name.isBlank()) {
            throw InvalidEquipmentException.blankName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
