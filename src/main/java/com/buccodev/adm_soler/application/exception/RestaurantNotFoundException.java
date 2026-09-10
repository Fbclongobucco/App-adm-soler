package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends ApplicationException {

    private RestaurantNotFoundException(String message) {
        super(message);
    }

    public static RestaurantNotFoundException withId(UUID id) {
        return new RestaurantNotFoundException("Restaurant with id '%s' was not found".formatted(id));
    }
}
