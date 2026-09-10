package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidAddressException;

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
        validate(street, city, state, zipCode, country);
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

    public static Address create(String street, String number, String complement, String neighborhood,
                                 String city, String state, String zipCode, String country) {
        LocalDateTime now = LocalDateTime.now();
        return new Address(UUID.randomUUID(), street, number, complement, neighborhood,
                city, state, zipCode, country, now, now);
    }

    public static Address restore(UUID id, String street, String number, String complement,
                                  String neighborhood, String city, String state, String zipCode,
                                  String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Address(id, street, number, complement, neighborhood, city, state, zipCode,
                country, createdAt, updatedAt);
    }

    public void update(String street, String number, String complement, String neighborhood,
                       String city, String state, String zipCode, String country) {
        validate(street, city, state, zipCode, country);
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.updatedAt = LocalDateTime.now();
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

    private static void validate(String street, String city, String state, String zipCode, String country) {
        if (isBlank(street)) {
            throw InvalidAddressException.blankStreet();
        }
        if (isBlank(city)) {
            throw InvalidAddressException.blankCity();
        }
        if (isBlank(state)) {
            throw InvalidAddressException.blankState();
        }
        if (isBlank(zipCode)) {
            throw InvalidAddressException.blankZipCode();
        }
        if (isBlank(country)) {
            throw InvalidAddressException.blankCountry();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
