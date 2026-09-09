package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.AccommodationJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccommodationJpaRepository extends JpaRepository<AccommodationJpa, UUID> {

    List<AccommodationJpa> findByProjectId(UUID projectId);

    List<AccommodationJpa> findByAddressId(UUID addressId);

    List<AccommodationJpa> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    List<AccommodationJpa> findByProjectIdAndStartDateBetween(UUID projectId, LocalDateTime start, LocalDateTime end);

    boolean existsByProjectId(UUID projectId);

    long countByProjectId(UUID projectId);

    /*
     * As consultas abaixo existem para eliminar o N+1 das leituras: os mappers de
     * dominio navegam pelas associacoes @ManyToOne, o que geraria um SELECT extra por
     * linha da pagina. Somente associacoes *-para-um entram no JOIN FETCH, portanto a
     * paginacao continua sendo resolvida no banco (LIMIT/OFFSET) e nao em memoria.
     */

    @Query(value = """
            SELECT a FROM AccommodationJpa a
            LEFT JOIN FETCH a.address
            LEFT JOIN FETCH a.project p
            LEFT JOIN FETCH p.client c
            LEFT JOIN FETCH c.address
            """,
            countQuery = "SELECT COUNT(a) FROM AccommodationJpa a")
    Page<AccommodationJpa> findAllWithRelations(Pageable pageable);

    @Query("""
            SELECT a FROM AccommodationJpa a
            LEFT JOIN FETCH a.address
            LEFT JOIN FETCH a.project p
            LEFT JOIN FETCH p.client c
            LEFT JOIN FETCH c.address
            WHERE a.id = :id
            """)
    Optional<AccommodationJpa> findByIdWithRelations(@Param("id") UUID id);
}
