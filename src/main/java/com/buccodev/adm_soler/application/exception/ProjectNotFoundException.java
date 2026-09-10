package com.buccodev.adm_soler.application.exception;

import java.util.UUID;

public class ProjectNotFoundException extends ApplicationException {

    private ProjectNotFoundException(String message) {
        super(message);
    }

    public static ProjectNotFoundException withId(UUID id) {
        return new ProjectNotFoundException("Project with id '%s' was not found".formatted(id));
    }
}
