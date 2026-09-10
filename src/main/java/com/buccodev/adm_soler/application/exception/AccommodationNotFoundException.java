package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class AccommodationNotFoundException extends ApplicationException {

    private AccommodationNotFoundException(String message) {
        super(message);
    }

    public static AccommodationNotFoundException withId(UUID id) {
        return new AccommodationNotFoundException("Accommodation with id '%s' was not found".formatted(id));
    }
}
