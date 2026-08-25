package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException userNotFound(UUID id) {
        return new ResourceNotFoundException("Usuario nao encontrado com id: " + id);
    }
}
