package com.buccodev.adm_soler.core.exception;

public class InvalidAccommodationException extends DomainException {

    private InvalidAccommodationException(String message) {
        super(message);
    }

    public static InvalidAccommodationException nullAddressId() {
        return new InvalidAccommodationException("Accommodation address must not be null");
    }

    public static InvalidAccommodationException nullProjectId() {
        return new InvalidAccommodationException("Accommodation project must not be null");
    }

    public static InvalidAccommodationException nonPositiveCapacity(Integer capacity) {
        return new InvalidAccommodationException(
                "Accommodation capacity must be greater than zero: " + capacity);
    }
}
