package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class ClientNotFoundException extends ApplicationException {

    private ClientNotFoundException(String message) {
        super(message);
    }

    public static ClientNotFoundException withId(UUID id) {
        return new ClientNotFoundException("Client with id '%s' was not found".formatted(id));
    }
}
