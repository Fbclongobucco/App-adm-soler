package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.BadRequestException;

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
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = validateName(name);
        this.description = description;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Equipment create(String name, String description) {
        var now = LocalDateTime.now();
        return new Equipment(UUID.randomUUID(), name, description, now, now);
    }

    public static Equipment restore(UUID id, String name, String description,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Equipment(id, name, description, createdAt, updatedAt);
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

    public void setName(String name) {
        this.name = validateName(name);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private String validateName(String name) {
        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new BadRequestException("name cannot be blank");
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
