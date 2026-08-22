package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.*;

public class Accommodation {

    private final UUID id;
    private Address address;
    private Project project;
    private Integer capacity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final Set<Employee> employees = new HashSet<>();
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Accommodation(UUID id, Address address, Project project, Integer capacity,
                          LocalDateTime startDate, LocalDateTime endDate,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.address = Objects.requireNonNull(address, "address is required");
        this.project = Objects.requireNonNull(project, "project is required");
        this.capacity = validateCapacity(capacity);
        this.startDate = validateStartDate(startDate);
        this.endDate = validateEndDate(endDate);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Accommodation create(Address address, Project project, Integer capacity,
                                       LocalDateTime startDate, LocalDateTime endDate) {
        var now = LocalDateTime.now();
        var accommodation = new Accommodation(UUID.randomUUID(), address, project, capacity,
                startDate, endDate, now, now);
        accommodation.validateDateRange(startDate, endDate);
        return accommodation;
    }

    public static Accommodation restore(UUID id, Address address, Project project, Integer capacity,
                                        LocalDateTime startDate, LocalDateTime endDate,
                                        Set<Employee> employees, LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {
        var accommodation = new Accommodation(id, address, project, capacity, startDate, endDate,
                createdAt, updatedAt);
        if (employees != null) {
            accommodation.employees.addAll(employees);
        }
        accommodation.validateDateRange(startDate, endDate);
        return accommodation;
    }

    public UUID getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public Project getProject() {
        return project;
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

    public Set<Employee> getEmployees() {
        return Collections.unmodifiableSet(employees);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setAddress(Address address) {
        this.address = Objects.requireNonNull(address, "address is required");
    }

    public void setProject(Project project) {
        this.project = Objects.requireNonNull(project, "project is required");
    }

    public void setCapacity(Integer capacity) {
        this.capacity = validateCapacity(capacity);
        if (employees.size() > this.capacity) {
            throw new BadRequestException("capacity cannot be less than current number of employees");
        }
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = validateStartDate(startDate);
        validateDateRange(this.startDate, this.endDate);
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = validateEndDate(endDate);
        validateDateRange(this.startDate, this.endDate);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "employee is required");
        if (capacity != null && employees.size() >= capacity) {
            throw new BadRequestException("accommodation capacity exceeded: " + capacity);
        }
        this.employees.add(employee);
    }

    public void addAllEmployees(Collection<Employee> employees) {
        Objects.requireNonNull(employees, "employees is required");
        if (capacity != null && this.employees.size() + employees.size() > capacity) {
            throw new BadRequestException("accommodation capacity exceeded: " + capacity);
        }
        this.employees.addAll(employees);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
    }

    private Integer validateCapacity(Integer capacity) {
        if (capacity != null && capacity <= 0) {
            throw new BadRequestException("capacity must be greater than zero");
        }
        return capacity;
    }

    private LocalDateTime validateStartDate(LocalDateTime startDate) {
        Objects.requireNonNull(startDate, "startDate is required");
        return startDate;
    }

    private LocalDateTime validateEndDate(LocalDateTime endDate) {
        Objects.requireNonNull(endDate, "endDate is required");
        return endDate;
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before endDate");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accommodation that = (Accommodation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
