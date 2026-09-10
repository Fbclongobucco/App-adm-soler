package com.buccodev.adm_soler.core.exception;

import java.math.BigDecimal;

public class InvalidRestaurantException extends DomainException {

    private InvalidRestaurantException(String message) {
        super(message);
    }

    public static InvalidRestaurantException blankName() {
        return new InvalidRestaurantException("Restaurant name must not be null or blank");
    }

    public static InvalidRestaurantException invalidEmail(String email) {
        return new InvalidRestaurantException("Restaurant email '%s' is not a valid email address".formatted(email));
    }

    public static InvalidRestaurantException invalidPhone(String phone) {
        return new InvalidRestaurantException("Restaurant phone '%s' is not a valid phone number".formatted(phone));
    }

    public static InvalidRestaurantException invalidCnpj(String cnpj) {
        return new InvalidRestaurantException("Restaurant CNPJ '%s' is not a valid CNPJ".formatted(cnpj));
    }

    public static InvalidRestaurantException negativePrice(String fieldName, BigDecimal value) {
        return new InvalidRestaurantException("Restaurant %s must not be negative: %s".formatted(fieldName, value));
    }

    public static InvalidRestaurantException nonPositiveDays(Integer days) {
        return new InvalidRestaurantException("Restaurant days must be greater than zero: " + days);
    }

    public static InvalidRestaurantException negativeEmployeeCount(int employeeCount) {
        return new InvalidRestaurantException("Employee count must not be negative: " + employeeCount);
    }
}
