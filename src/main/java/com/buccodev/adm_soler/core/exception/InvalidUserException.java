package com.buccodev.adm_soler.core.exception;

public class InvalidUserException extends DomainException {

    private InvalidUserException(String message) {
        super(message);
    }

    public static InvalidUserException blankName() {
        return new InvalidUserException("User name must not be null or blank");
    }

    public static InvalidUserException invalidEmail(String email) {
        return new InvalidUserException("User email '%s' is not a valid email address".formatted(email));
    }

    public static InvalidUserException blankPassword() {
        return new InvalidUserException("User password must not be null or blank");
    }

    public static InvalidUserException shortPassword(int minLength) {
        return new InvalidUserException("User password must be at least %d characters".formatted(minLength));
    }

    public static InvalidUserException invalidPhone(String phone) {
        return new InvalidUserException("User phone '%s' is not a valid phone number".formatted(phone));
    }

    public static InvalidUserException nullRole() {
        return new InvalidUserException("User role must not be null");
    }
}
