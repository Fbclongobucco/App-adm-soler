package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "addresses")
public class AddressJpa implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String street;

    private String number;

    private String complement;

    private String neighborhood;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity = true;

    public AddressJpa() {
    }

    public AddressJpa(UUID id, String street, String number, String complement, String neighborhood,
                      String city, String state, String zipCode, String country,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    public void markAsExisting() {
        this.newEntity = false;
    }

    @Override
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
