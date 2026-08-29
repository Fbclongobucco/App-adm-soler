package com.buccodev.adm_soler.core.repository;

public record PageQuery(int page, int size) {
    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException("page nao pode ser negativo");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size deve ser maior que zero");
        }
    }
}
