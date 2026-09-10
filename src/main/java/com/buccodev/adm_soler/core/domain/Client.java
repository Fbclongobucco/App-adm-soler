package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidClientException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Client {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private String cnpj;
    private UUID addressId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Client(UUID id, String name, String email, String phone, String cnpj,
                   UUID addressId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        validate(name, email, phone, cnpj, addressId);
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cnpj = cnpj;
        this.addressId = addressId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Client create(String name, String email, String phone, String cnpj, UUID addressId) {
        LocalDateTime now = LocalDateTime.now();
        return new Client(UUID.randomUUID(), name, email, phone, cnpj, addressId, now, now);
    }

    public static Client restore(UUID id, String name, String email, String phone, String cnpj,
                                 UUID addressId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Client(id, name, email, phone, cnpj, addressId, createdAt, updatedAt);
    }

    public void update(String name, String email, String phone, String cnpj, UUID addressId) {
        validate(name, email, phone, cnpj, addressId);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cnpj = cnpj;
        this.addressId = addressId;
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

    public String getCnpj() {
        return cnpj;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    private static void validate(String name, String email, String phone, String cnpj, UUID addressId) {
        if (name == null || name.isBlank()) {
            throw InvalidClientException.blankName();
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw InvalidClientException.invalidEmail(email);
        }
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw InvalidClientException.invalidPhone(phone);
        }
        if (cnpj != null && !CNPJ_PATTERN.matcher(cnpj).matches()) {
            throw InvalidClientException.invalidCnpj(cnpj);
        }
        if (addressId == null) {
            throw InvalidClientException.nullAddressId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
