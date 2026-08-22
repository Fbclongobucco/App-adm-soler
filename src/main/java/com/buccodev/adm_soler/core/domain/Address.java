package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Address {

    private final UUID id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Address(UUID id, String street, String number, String complement, String neighborhood,
                    String city, String state, String zipCode, String country,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.street = validateStreet(street);
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = validateCity(city);
        this.state = validateState(state);
        this.zipCode = validateZipCode(zipCode);
        this.country = validateCountry(country);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Address create(String street, String number, String complement, String neighborhood,
                                 String city, String state, String zipCode, String country) {
        var now = LocalDateTime.now();
        return new Address(UUID.randomUUID(), street, number, complement, neighborhood,
                city, state, zipCode, country, now, now);
    }

    public static Address restore(UUID id, String street, String number, String complement,
                                  String neighborhood, String city, String state, String zipCode,
                                  String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Address(id, street, number, complement, neighborhood, city, state, zipCode,
                country, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStreet(String street) {
        this.street = validateStreet(street);
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setCity(String city) {
        this.city = validateCity(city);
    }

    public void setState(String state) {
        this.state = validateState(state);
    }

    public void setZipCode(String zipCode) {
        this.zipCode = validateZipCode(zipCode);
    }

    public void setCountry(String country) {
        this.country = validateCountry(country);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private String validateStreet(String street) {
        Objects.requireNonNull(street, "street is required");
        if (street.isBlank()) {
            throw new BadRequestException("street cannot be blank");
        }
        return street;
    }

    private String validateCity(String city) {
        Objects.requireNonNull(city, "city is required");
        if (city.isBlank()) {
            throw new BadRequestException("city cannot be blank");
        }
        return city;
    }

    private String validateState(String state) {
        Objects.requireNonNull(state, "state is required");
        if (state.isBlank()) {
            throw new BadRequestException("state cannot be blank");
        }
        return state;
    }

    private String validateZipCode(String zipCode) {
        Objects.requireNonNull(zipCode, "zipCode is required");
        if (zipCode.isBlank()) {
            throw new BadRequestException("zipCode cannot be blank");
        }
        return zipCode;
    }

    private String validateCountry(String country) {
        Objects.requireNonNull(country, "country is required");
        if (country.isBlank()) {
            throw new BadRequestException("country cannot be blank");
        }
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
