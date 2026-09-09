package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.ClientJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientJpa, UUID> {

    Optional<ClientJpa> findByEmail(String email);

    Optional<ClientJpa> findByCnpj(String cnpj);

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);

    List<ClientJpa> findByNameContainingIgnoreCase(String name);

    List<ClientJpa> findByAddressId(UUID addressId);

    /*
     * As consultas abaixo existem para eliminar o N+1 das leituras: os mappers de
     * dominio navegam pelas associacoes @ManyToOne, o que geraria um SELECT extra por
     * linha da pagina. Somente associacoes *-para-um entram no JOIN FETCH, portanto a
     * paginacao continua sendo resolvida no banco (LIMIT/OFFSET) e nao em memoria.
     */

    @Query(value = """
            SELECT c FROM ClientJpa c
            LEFT JOIN FETCH c.address
            """,
            countQuery = "SELECT COUNT(c) FROM ClientJpa c")
    Page<ClientJpa> findAllWithRelations(Pageable pageable);

    @Query("""
            SELECT c FROM ClientJpa c
            LEFT JOIN FETCH c.address
            WHERE c.id = :id
            """)
    Optional<ClientJpa> findByIdWithRelations(@Param("id") UUID id);
}
