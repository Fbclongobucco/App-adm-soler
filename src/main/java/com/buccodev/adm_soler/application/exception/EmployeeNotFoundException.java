package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends ApplicationException {

    private EmployeeNotFoundException(String message) {
        super(message);
    }

    public static EmployeeNotFoundException withId(UUID id) {
        return new EmployeeNotFoundException("Employee with id '%s' was not found".formatted(id));
    }
}
