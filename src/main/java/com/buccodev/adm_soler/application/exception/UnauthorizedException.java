package com.buccodev.adm_soler.application.exception;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Falha de credencial no login.
     *
     * Mensagem unica de proposito, para senha errada e para email inexistente:
     * respostas diferentes contariam a quem tenta adivinhar quais emails estao
     * cadastrados.
     */
    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Email ou senha invalidos");
    }

    /** Refresh token desconhecido, revogado por logout ou por troca de senha. */
    public static UnauthorizedException invalidRefreshToken() {
        return new UnauthorizedException("Refresh token invalido");
    }

    public static UnauthorizedException expiredRefreshToken() {
        return new UnauthorizedException("Refresh token expirado");
    }

    /**
     * Reuso de um refresh token ja rotacionado: duas partes conhecem o mesmo
     * segredo, entao todas as sessoes do usuario caem junto.
     */
    public static UnauthorizedException refreshTokenReused() {
        return new UnauthorizedException("Refresh token ja utilizado; sessoes revogadas");
    }

    /** Troca de senha em que a senha atual informada nao confere. */
    public static UnauthorizedException invalidCurrentPassword() {
        return new UnauthorizedException("Senha atual invalida");
    }
}
