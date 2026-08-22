package com.buccodev.adm_soler.core.exception;

public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NotFoundException with(Class<?> clazz, Object id) {
        return new NotFoundException(clazz.getSimpleName() + " with id " + id + " not found");
    }

}
