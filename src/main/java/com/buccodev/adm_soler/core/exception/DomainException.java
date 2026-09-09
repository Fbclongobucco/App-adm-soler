package com.buccodev.adm_soler.core.exception;

/**
 * Erro de invariante de dominio. Lancada pelas entidades de {@code core.domain}
 * quando uma regra de negocio e violada, sem depender de nenhuma camada externa.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
