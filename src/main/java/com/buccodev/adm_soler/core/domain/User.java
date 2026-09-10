package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidUserException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class User {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");

    private final UUID id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private final Role role;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        ADMIN, USER, FOREIGN;
    }

    private User(UUID id, String name, String email, String password, String phone, Role role,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        validate(name, email, password, phone, role);
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Recebe a senha ja com hash: quem chama e responsavel por aplicar o
     * {@link com.buccodev.adm_soler.core.security.PasswordHasher}. O tamanho minimo da
     * senha em claro e contrato de entrada, validado no DTO de request.
     */
    public static User create(String name, String email, String hashedPassword, String phone) {
        LocalDateTime now = LocalDateTime.now();
        return new User(UUID.randomUUID(), name, email, hashedPassword, phone, Role.USER, now, now);
    }

    public static User createAdmin(String name, String email, String hashedPassword, String phone) {
        LocalDateTime now = LocalDateTime.now();
        return new User(UUID.randomUUID(), name, email, hashedPassword, phone, Role.ADMIN, now, now);
    }

    public static User restore(UUID id, String name, String email, String password, String phone,
                               Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, name, email, password, phone, role, createdAt, updatedAt);
    }

    public void update(String name, String email, String hashedPassword, String phone) {
        validate(name, email, hashedPassword, phone, role);
        this.name = name;
        this.email = email;
        this.password = hashedPassword;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
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

    private static void validate(String name, String email, String password, String phone, Role role) {
        if (name == null || name.isBlank()) {
            throw InvalidUserException.blankName();
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw InvalidUserException.invalidEmail(email);
        }
        if (password == null || password.isBlank()) {
            throw InvalidUserException.blankPassword();
        }
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw InvalidUserException.invalidPhone(phone);
        }
        if (role == null) {
            throw InvalidUserException.nullRole();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
