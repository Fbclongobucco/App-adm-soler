package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidEmployeeException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Employee {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private UUID addressId;
    private String role;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Employee(UUID id, String name, String email, String phone, UUID addressId,
                     String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        validate(name, email, phone, addressId, role);
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addressId = addressId;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Employee create(String name, String email, String phone, UUID addressId, String role) {
        LocalDateTime now = LocalDateTime.now();
        return new Employee(UUID.randomUUID(), name, email, phone, addressId, role, now, now);
    }

    public static Employee restore(UUID id, String name, String email, String phone, UUID addressId,
                                   String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Employee(id, name, email, phone, addressId, role, createdAt, updatedAt);
    }

    public void update(String name, String email, String phone, UUID addressId, String role) {
        validate(name, email, phone, addressId, role);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addressId = addressId;
        this.role = role;
        this.updatedAt = LocalDateTime.now();
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

    public UUID getAddressId() {
        return addressId;
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

    private static void validate(String name, String email, String phone, UUID addressId, String role) {
        if (name == null || name.isBlank()) {
            throw InvalidEmployeeException.blankName();
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw InvalidEmployeeException.invalidEmail(email);
        }
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw InvalidEmployeeException.invalidPhone(phone);
        }
        if (addressId == null) {
            throw InvalidEmployeeException.nullAddressId();
        }
        if (role == null || role.isBlank()) {
            throw InvalidEmployeeException.blankRole();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
