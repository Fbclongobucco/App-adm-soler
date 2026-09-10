package com.buccodev.adm_soler.core.exception;

public class InvalidEquipmentException extends DomainException {

    private InvalidEquipmentException(String message) {
        super(message);
    }

    public static InvalidEquipmentException blankName() {
        return new InvalidEquipmentException("Equipment name must not be null or blank");
    }
}
