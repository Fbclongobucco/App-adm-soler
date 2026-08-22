package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class User {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");

    private final UUID id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        ADMIN, USER, FOREIGN;
    }

    private User(UUID id, String name, String email, String password, String phone, Role role,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.password = Objects.requireNonNull(password, "password is required");
        this.phone = validatePhone(phone);
        this.role = Objects.requireNonNull(role, "role is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static User create(String name, String email, String password, String phone) {
        var now = LocalDateTime.now();
        return new User(UUID.randomUUID(), name, email, password, phone, Role.USER, now, now);
    }

    public static User restore(UUID id, String name, String email, String password, String phone,
                               Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, name, email, password, phone, role, createdAt, updatedAt);
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

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
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

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "password is required");
    }

    public void setPhone(String phone) {
        this.phone = validatePhone(phone);
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role is required");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
