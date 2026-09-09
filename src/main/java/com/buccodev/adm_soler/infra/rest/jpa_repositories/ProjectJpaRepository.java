package com.buccodev.adm_soler.infra.rest.jpa_repositories;

import com.buccodev.adm_soler.infra.rest.entities.ProjectJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectJpa, UUID> {

    List<ProjectJpa> findByClientId(UUID clientId);

    List<ProjectJpa> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByClientId(UUID clientId);

    long countByClientId(UUID clientId);

    List<ProjectJpa> findByServiceProvidedContainingIgnoreCase(String serviceProvided);

    Optional<ProjectJpa> findByOs(String os);

    /*
     * As consultas abaixo existem para eliminar o N+1 das leituras: os mappers de
     * dominio navegam pelas associacoes @ManyToOne, o que geraria um SELECT extra por
     * linha da pagina. Somente associacoes *-para-um entram no JOIN FETCH, portanto a
     * paginacao continua sendo resolvida no banco (LIMIT/OFFSET) e nao em memoria.
     */

    @Query(value = """
            SELECT p FROM ProjectJpa p
            LEFT JOIN FETCH p.client c
            LEFT JOIN FETCH c.address
            """,
            countQuery = "SELECT COUNT(p) FROM ProjectJpa p")
    Page<ProjectJpa> findAllWithRelations(Pageable pageable);

    @Query("""
            SELECT p FROM ProjectJpa p
            LEFT JOIN FETCH p.client c
            LEFT JOIN FETCH c.address
            WHERE p.id = :id
            """)
    Optional<ProjectJpa> findByIdWithRelations(@Param("id") UUID id);
}
