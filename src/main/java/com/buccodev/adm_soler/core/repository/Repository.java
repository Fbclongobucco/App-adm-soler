package com.buccodev.adm_soler.core.repository;

import java.util.Optional;

/**
 * Contrato de persistencia comum a todos os agregados. A paginacao vive aqui,
 * como tipo aninhado do proprio contrato, para nao virar um pacote a parte.
 */
public interface Repository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    PageResult<T> findAll(PageQuery pageQuery);

    void delete(T entity);

    boolean existsById(ID id);

    record PageQuery(int page, int size) {
        public PageQuery {
            if (page < 0) {
                throw new IllegalArgumentException("page nao pode ser negativo");
            }
            if (size < 1) {
                throw new IllegalArgumentException("size deve ser maior que zero");
            }
        }
    }

    record PageResult<T>(java.util.List<T> content, int page, int size, long totalElements, int totalPages) {
    }
}
