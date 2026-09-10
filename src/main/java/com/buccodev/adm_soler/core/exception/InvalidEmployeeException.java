package com.buccodev.adm_soler.core.exception;

public class InvalidEmployeeException extends DomainException {

    private InvalidEmployeeException(String message) {
        super(message);
    }

    public static InvalidEmployeeException blankName() {
        return new InvalidEmployeeException("Employee name must not be null or blank");
    }

    public static InvalidEmployeeException invalidEmail(String email) {
        return new InvalidEmployeeException("Employee email '%s' is not a valid email address".formatted(email));
    }

    public static InvalidEmployeeException invalidPhone(String phone) {
        return new InvalidEmployeeException("Employee phone '%s' is not a valid phone number".formatted(phone));
    }

    public static InvalidEmployeeException nullAddressId() {
        return new InvalidEmployeeException("Employee address must not be null");
    }

    public static InvalidEmployeeException blankRole() {
        return new InvalidEmployeeException("Employee role must not be null or blank");
    }
}
