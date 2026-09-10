package com.buccodev.adm_soler.core.exception;

public class InvalidClientException extends DomainException {

    private InvalidClientException(String message) {
        super(message);
    }

    public static InvalidClientException blankName() {
        return new InvalidClientException("Client name must not be null or blank");
    }

    public static InvalidClientException invalidEmail(String email) {
        return new InvalidClientException("Client email '%s' is not a valid email address".formatted(email));
    }

    public static InvalidClientException invalidPhone(String phone) {
        return new InvalidClientException("Client phone '%s' is not a valid phone number".formatted(phone));
    }

    public static InvalidClientException invalidCnpj(String cnpj) {
        return new InvalidClientException("Client CNPJ '%s' is not a valid CNPJ".formatted(cnpj));
    }

    public static InvalidClientException nullAddressId() {
        return new InvalidClientException("Client address must not be null");
    }
}
