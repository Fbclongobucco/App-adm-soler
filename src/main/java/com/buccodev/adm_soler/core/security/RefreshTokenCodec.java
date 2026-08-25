package com.buccodev.adm_soler.core.security;

/**
 * Porta de geracao e digestao dos refresh tokens.
 *
 * O refresh token e um valor opaco aleatorio (nao um JWT): ele precisa ser
 * revogavel, e um JWT auto-contido nao e. O cliente recebe o valor em claro e
 * o banco guarda apenas o {@link #fingerprint(String)} dele.
 */
public interface RefreshTokenCodec {

    /** Novo valor aleatorio em claro, entregue ao cliente uma unica vez. */
    String newValue();

    /** Digest deterministico do valor em claro, usado para buscar e persistir. */
    String fingerprint(String value);
}
