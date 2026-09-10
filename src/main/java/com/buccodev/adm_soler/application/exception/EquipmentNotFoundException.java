package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class EquipmentNotFoundException extends ApplicationException {

    private EquipmentNotFoundException(String message) {
        super(message);
    }

    public static EquipmentNotFoundException withId(UUID id) {
        return new EquipmentNotFoundException("Equipment with id '%s' was not found".formatted(id));
    }
}
