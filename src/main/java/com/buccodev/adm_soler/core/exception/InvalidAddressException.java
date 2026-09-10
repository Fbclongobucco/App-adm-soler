package com.buccodev.adm_soler.core.exception;

public class InvalidAddressException extends DomainException {

    private InvalidAddressException(String message) {
        super(message);
    }

    public static InvalidAddressException blankStreet() {
        return new InvalidAddressException("Address street must not be null or blank");
    }

    public static InvalidAddressException blankCity() {
        return new InvalidAddressException("Address city must not be null or blank");
    }

    public static InvalidAddressException blankState() {
        return new InvalidAddressException("Address state must not be null or blank");
    }

    public static InvalidAddressException blankZipCode() {
        return new InvalidAddressException("Address zip code must not be null or blank");
    }

    public static InvalidAddressException blankCountry() {
        return new InvalidAddressException("Address country must not be null or blank");
    }
}
