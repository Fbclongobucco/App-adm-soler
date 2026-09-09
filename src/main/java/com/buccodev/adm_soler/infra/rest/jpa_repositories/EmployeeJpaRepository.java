package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.EmployeeJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpa, UUID> {

    Optional<EmployeeJpa> findByEmail(String email);

    boolean existsByEmail(String email);

    List<EmployeeJpa> findByNameContainingIgnoreCase(String name);

    List<EmployeeJpa> findByRole(String role);

    List<EmployeeJpa> findByAddressId(UUID addressId);

    /*
     * As consultas abaixo existem para eliminar o N+1 das leituras: os mappers de
     * dominio navegam pelas associacoes @ManyToOne, o que geraria um SELECT extra por
     * linha da pagina. Somente associacoes *-para-um entram no JOIN FETCH, portanto a
     * paginacao continua sendo resolvida no banco (LIMIT/OFFSET) e nao em memoria.
     */

    @Query(value = """
            SELECT e FROM EmployeeJpa e
            LEFT JOIN FETCH e.address
            """,
            countQuery = "SELECT COUNT(e) FROM EmployeeJpa e")
    Page<EmployeeJpa> findAllWithRelations(Pageable pageable);

    @Query("""
            SELECT e FROM EmployeeJpa e
            LEFT JOIN FETCH e.address
            WHERE e.id = :id
            """)
    Optional<EmployeeJpa> findByIdWithRelations(@Param("id") UUID id);
}
