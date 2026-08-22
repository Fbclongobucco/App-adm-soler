package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.*;

public class Project {

    private final UUID id;
    private String os;
    private String serviceProvided;
    private Client client;
    private final Set<Restaurant> restaurants = new HashSet<>();
    private final Set<Accommodation> accommodations = new HashSet<>();
    private final Set<Employee> employees = new HashSet<>();
    private final Set<Equipment> equipments = new HashSet<>();
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Project(UUID id, String os, String serviceProvided, Client client,
                    LocalDateTime startDate, LocalDateTime endDate,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.os = validateOs(os);
        this.serviceProvided = validateServiceProvided(serviceProvided);
        this.client = client;
        this.startDate = validateStartDate(startDate);
        this.endDate = validateEndDate(endDate);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Project create(String os, String serviceProvided, Client client,
                                 LocalDateTime startDate, LocalDateTime endDate) {
        var now = LocalDateTime.now();
        var project = new Project(UUID.randomUUID(), os, serviceProvided, client,
                startDate, endDate, now, now);
        project.validateDateRange(startDate, endDate);
        return project;
    }

    public static Project restore(UUID id, String os, String serviceProvided, Client client,
                                  Set<Restaurant> restaurants, Set<Accommodation> accommodations,
                                  Set<Employee> employees, Set<Equipment> equipments,
                                  LocalDateTime startDate, LocalDateTime endDate,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        var project = new Project(id, os, serviceProvided, client, startDate, endDate,
                createdAt, updatedAt);
        if (restaurants != null) {
            project.restaurants.addAll(restaurants);
        }
        if (accommodations != null) {
            project.accommodations.addAll(accommodations);
        }
        if (employees != null) {
            project.employees.addAll(employees);
        }
        if (equipments != null) {
            project.equipments.addAll(equipments);
        }
        project.validateDateRange(startDate, endDate);
        return project;
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

    public Client getClient() {
        return client;
    }

    public Set<Restaurant> getRestaurants() {
        return Collections.unmodifiableSet(restaurants);
    }

    public Set<Accommodation> getAccommodations() {
        return Collections.unmodifiableSet(accommodations);
    }

    public Set<Employee> getEmployees() {
        return Collections.unmodifiableSet(employees);
    }

    public Set<Equipment> getEquipments() {
        return Collections.unmodifiableSet(equipments);
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

    public void setOs(String os) {
        this.os = validateOs(os);
    }

    public void setServiceProvided(String serviceProvided) {
        this.serviceProvided = validateServiceProvided(serviceProvided);
    }

    public void setClient(Client client) {
        this.client = client;
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

    public void addRestaurant(Restaurant restaurant) {
        Objects.requireNonNull(restaurant, "restaurant is required");
        this.restaurants.add(restaurant);
    }

    public void addAllRestaurants(Collection<Restaurant> restaurants) {
        Objects.requireNonNull(restaurants, "restaurants is required");
        this.restaurants.addAll(restaurants);
    }

    public void removeRestaurant(Restaurant restaurant) {
        this.restaurants.remove(restaurant);
    }

    public void addAccommodation(Accommodation accommodation) {
        Objects.requireNonNull(accommodation, "accommodation is required");
        this.accommodations.add(accommodation);
    }

    public void addAllAccommodations(Collection<Accommodation> accommodations) {
        Objects.requireNonNull(accommodations, "accommodations is required");
        this.accommodations.addAll(accommodations);
    }

    public void removeAccommodation(Accommodation accommodation) {
        this.accommodations.remove(accommodation);
    }

    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "employee is required");
        this.employees.add(employee);
    }

    public void addAllEmployees(Collection<Employee> employees) {
        Objects.requireNonNull(employees, "employees is required");
        this.employees.addAll(employees);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
    }

    public void addEquipment(Equipment equipment) {
        Objects.requireNonNull(equipment, "equipment is required");
        this.equipments.add(equipment);
    }

    public void addAllEquipments(Collection<Equipment> equipments) {
        Objects.requireNonNull(equipments, "equipments is required");
        this.equipments.addAll(equipments);
    }

    public void removeEquipment(Equipment equipment) {
        this.equipments.remove(equipment);
    }

    private String validateOs(String os) {
        Objects.requireNonNull(os, "os is required");
        if (os.isBlank()) {
            throw new BadRequestException("os cannot be blank");
        }
        return os;
    }

    private String validateServiceProvided(String serviceProvided) {
        Objects.requireNonNull(serviceProvided, "serviceProvided is required");
        if (serviceProvided.isBlank()) {
            throw new BadRequestException("serviceProvided cannot be blank");
        }
        return serviceProvided;
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
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
