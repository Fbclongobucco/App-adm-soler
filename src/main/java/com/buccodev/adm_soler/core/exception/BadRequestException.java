package com.buccodev.adm_soler.core.exception;

public class BadRequestException extends DomainException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
