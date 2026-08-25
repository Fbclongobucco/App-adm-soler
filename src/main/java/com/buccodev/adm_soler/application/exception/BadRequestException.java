package com.buccodev.adm_soler.application.exception;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(message);
    }

    public static BadRequestException missingLoginCredentials() {
        return new BadRequestException("email e password sao obrigatorios");
    }

    public static BadRequestException missingRefreshToken() {
        return new BadRequestException("refreshToken e obrigatorio");
    }

    public static BadRequestException missingPasswordChangeFields() {
        return new BadRequestException("currentPassword e newPassword sao obrigatorios");
    }

    public static BadRequestException newPasswordMustDiffer() {
        return new BadRequestException("A nova senha deve ser diferente da atual");
    }
}
