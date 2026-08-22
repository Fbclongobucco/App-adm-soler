package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class Client {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private String cnpj;
    private Address address;
    private final Set<Project> projects = new HashSet<>();
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Client(UUID id, String name, String email, String phone, String cnpj,
                   Address address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.phone = validatePhone(phone);
        this.cnpj = validateCnpj(cnpj);
        this.address = address;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Client create(String name, String email, String phone, String cnpj, Address address) {
        var now = LocalDateTime.now();
        return new Client(UUID.randomUUID(), name, email, phone, cnpj, address, now, now);
    }

    public static Client restore(UUID id, String name, String email, String phone, String cnpj,
                                 Address address, Set<Project> projects, LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {
        var client = new Client(id, name, email, phone, cnpj, address, createdAt, updatedAt);
        if (projects != null) {
            client.projects.addAll(projects);
        }
        return client;
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

    public String getCnpj() {
        return cnpj;
    }

    public Address getAddress() {
        return address;
    }

    public Set<Project> getProjects() {
        return Collections.unmodifiableSet(projects);
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

    public void setCnpj(String cnpj) {
        this.cnpj = validateCnpj(cnpj);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addProject(Project project) {
        Objects.requireNonNull(project, "project is required");
        this.projects.add(project);
    }

    public void addAllProjects(Collection<Project> projects) {
        Objects.requireNonNull(projects, "projects is required");
        this.projects.addAll(projects);
    }

    public void removeProject(Project project) {
        this.projects.remove(project);
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

    private String validateCnpj(String cnpj) {
        if (cnpj != null && !CNPJ_PATTERN.matcher(cnpj).matches()) {
            throw new BadRequestException("invalid CNPJ format");
        }
        return cnpj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
