package com.buccodev.adm_soler.application.dto.project;

import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Criacao e edicao de uma obra, com os vinculos no mesmo corpo.
 *
 * Os quatro conjuntos de ids sao opcionais e {@code null} significa "nao mexa":
 * a tela de edicao manda so os campos proprios da obra e nao pode desvincular
 * nada por omissao.
 *
 * As semanticas diferem porque os lados do relacionamento diferem:
 * <ul>
 *   <li>{@code employeeIds} e {@code equipmentIds} sao ManyToMany com a obra do
 *       lado dono, entao a lista enviada <b>substitui</b> o vinculo.</li>
 *   <li>{@code restaurantIds} e {@code accommodationIds} vivem no filho
 *       (restaurante e alojamento apontam para uma obra), entao a lista apenas
 *       <b>vincula</b> os informados. Desvincular alojamento nem seria possivel:
 *       a obra e obrigatoria nele.</li>
 * </ul>
 */
public record ProjectRequest(
        String os,
        String serviceProvided,
        UUID clientId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Set<UUID> restaurantIds,
        Set<UUID> accommodationIds,
        Set<UUID> employeeIds,
        Set<UUID> equipmentIds
) {
    public Project toDomain(Client client) {
        return Project.create(
                os,
                serviceProvided,
                client,
                startDate,
                endDate
        );
    }
}
