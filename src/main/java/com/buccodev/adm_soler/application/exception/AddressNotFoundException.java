package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class AddressNotFoundException extends ApplicationException {

    private AddressNotFoundException(String message) {
        super(message);
    }

    public static AddressNotFoundException withId(UUID id) {
        return new AddressNotFoundException("Address with id '%s' was not found".formatted(id));
    }
}
