package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidPeriodException;
import com.buccodev.adm_soler.core.exception.InvalidProjectException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Project {

    private final UUID id;
    private String os;
    private String serviceProvided;
    private UUID clientId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Project(UUID id, String os, String serviceProvided, UUID clientId,
                    LocalDateTime startDate, LocalDateTime endDate,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        validate(os, serviceProvided, clientId, startDate, endDate);
        this.id = id;
        this.os = os;
        this.serviceProvided = serviceProvided;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Project create(String os, String serviceProvided, UUID clientId,
                                 LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        return new Project(UUID.randomUUID(), os, serviceProvided, clientId, startDate, endDate, now, now);
    }

    public static Project restore(UUID id, String os, String serviceProvided, UUID clientId,
                                  LocalDateTime startDate, LocalDateTime endDate,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Project(id, os, serviceProvided, clientId, startDate, endDate, createdAt, updatedAt);
    }

    public void update(String os, String serviceProvided, UUID clientId,
                       LocalDateTime startDate, LocalDateTime endDate) {
        validate(os, serviceProvided, clientId, startDate, endDate);
        this.os = os;
        this.serviceProvided = serviceProvided;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getOs() {
        return os;
    }

    public String getServiceProvided() {
        return serviceProvided;
    }

    public UUID getClientId() {
        return clientId;
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

    private static void validate(String os, String serviceProvided, UUID clientId,
                                 LocalDateTime startDate, LocalDateTime endDate) {
        if (os == null || os.isBlank()) {
            throw InvalidProjectException.blankOs();
        }
        if (serviceProvided == null || serviceProvided.isBlank()) {
            throw InvalidProjectException.blankServiceProvided();
        }
        if (clientId == null) {
            throw InvalidProjectException.nullClientId();
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
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
