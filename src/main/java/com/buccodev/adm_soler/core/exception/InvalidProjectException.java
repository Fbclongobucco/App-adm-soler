package com.buccodev.adm_soler.core.exception;

public class InvalidProjectException extends DomainException {

    private InvalidProjectException(String message) {
        super(message);
    }

    public static InvalidProjectException blankOs() {
        return new InvalidProjectException("Project OS must not be null or blank");
    }

    public static InvalidProjectException blankServiceProvided() {
        return new InvalidProjectException("Project service provided must not be null or blank");
    }

    public static InvalidProjectException nullClientId() {
        return new InvalidProjectException("Project client must not be null");
    }
}
