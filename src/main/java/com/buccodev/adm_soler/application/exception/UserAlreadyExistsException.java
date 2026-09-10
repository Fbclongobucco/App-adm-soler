package com.buccodev.adm_soler.application.exception;

public class UserAlreadyExistsException extends ApplicationException {

    private UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException withEmail(String email) {
        return new UserAlreadyExistsException("User email '%s' is already in use".formatted(email));
    }
}
