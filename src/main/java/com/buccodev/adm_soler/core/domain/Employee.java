package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Employee {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private Address address;
    private String role;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Employee(UUID id, String name, String email, String phone, Address address,
                     String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.phone = validatePhone(phone);
        this.address = address;
        this.role = validateRole(role);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Employee create(String name, String email, String phone, Address address, String role) {
        var now = LocalDateTime.now();
        return new Employee(UUID.randomUUID(), name, email, phone, address, role, now, now);
    }

    public static Employee restore(UUID id, String name, String email, String phone, Address address,
                                   String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Employee(id, name, email, phone, address, role, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    public String getRole() {
        return role;
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

    public void setEmail(String email) {
        this.email = validateEmail(email);
    }

    public void setPhone(String phone) {
        this.phone = validatePhone(phone);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setRole(String role) {
        this.role = validateRole(role);
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

    private String validateEmail(String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("invalid email format");
        }
        return email;
    }

    private String validatePhone(String phone) {
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BadRequestException("invalid phone format");
        }
        return phone;
    }

    private String validateRole(String role) {
        Objects.requireNonNull(role, "role is required");
        if (role.isBlank()) {
            throw new BadRequestException("role cannot be blank");
        }
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
