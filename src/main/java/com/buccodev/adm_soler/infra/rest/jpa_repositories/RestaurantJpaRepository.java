package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.RestaurantJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantJpa, UUID> {

    List<RestaurantJpa> findByProjectId(UUID projectId);

    List<RestaurantJpa> findByIsBilled(Boolean isBilled);

    Optional<RestaurantJpa> findByEmail(String email);

    Optional<RestaurantJpa> findByCnpj(String cnpj);

    boolean existsByProjectId(UUID projectId);

    List<RestaurantJpa> findByAddressId(UUID addressId);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM RestaurantJpa r WHERE r.project.id = :projectId")
    BigDecimal sumTotalByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(SUM(r.total), 0) FROM RestaurantJpa r WHERE r.isBilled = :isBilled")
    BigDecimal sumTotalByIsBilled(@Param("isBilled") Boolean isBilled);

    /*
     * As consultas abaixo existem para eliminar o N+1 das leituras: os mappers de
     * dominio navegam pelas associacoes @ManyToOne, o que geraria um SELECT extra por
     * linha da pagina. Somente associacoes *-para-um entram no JOIN FETCH, portanto a
     * paginacao continua sendo resolvida no banco (LIMIT/OFFSET) e nao em memoria.
     */

    @Query(value = """
            SELECT r FROM RestaurantJpa r
            LEFT JOIN FETCH r.project p
            LEFT JOIN FETCH p.client c
            LEFT JOIN FETCH c.address
            LEFT JOIN FETCH r.address
            """,
            countQuery = "SELECT COUNT(r) FROM RestaurantJpa r")
    Page<RestaurantJpa> findAllWithRelations(Pageable pageable);

    @Query("""
            SELECT r FROM RestaurantJpa r
            LEFT JOIN FETCH r.project p
            LEFT JOIN FETCH p.client c
            LEFT JOIN FETCH c.address
            LEFT JOIN FETCH r.address
            WHERE r.id = :id
            """)
    Optional<RestaurantJpa> findByIdWithRelations(@Param("id") UUID id);
}
