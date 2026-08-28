package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.infra.rest.entities.AccommodationJpa;
import com.buccodev.adm_soler.infra.rest.entities.EmployeeJpa;
import com.buccodev.adm_soler.infra.rest.entities.EquipmentJpa;
import com.buccodev.adm_soler.infra.rest.entities.ProjectJpa;
import com.buccodev.adm_soler.infra.rest.entities.RestaurantJpa;

import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectJpa toJpa(Project domain) {
        if (domain == null) return null;
        ProjectJpa jpa = new ProjectJpa();
        jpa.setId(domain.getId());
        jpa.setOs(domain.getOs());
        jpa.setServiceProvided(domain.getServiceProvided());
        jpa.setClient(ClientMapper.toJpa(domain.getClient()));
        // Restaurantes e alojamentos NAO entram aqui: eles sao mapeados por
        // `mappedBy` e quem guarda a coluna project_id e o filho. Ja funcionarios
        // e equipamentos sao ManyToMany com a obra do lado dono, entao e este
        // save que escreve as tabelas de juncao.
        jpa.setEmployees(domain.getEmployees().stream()
                .map(EmployeeMapper::toJpa)
                .collect(Collectors.toCollection(java.util.HashSet::new)));
        jpa.setEquipments(domain.getEquipments().stream()
                .map(EquipmentMapper::toJpa)
                .collect(Collectors.toCollection(java.util.HashSet::new)));
        jpa.setStartDate(domain.getStartDate());
        jpa.setEndDate(domain.getEndDate());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    /**
     * Monta a obra com tudo que esta ligado a ela.
     *
     * Os filhos sao construidos aqui, e nao pelos mappers deles, de proposito:
     * {@code RestaurantMapper.toDomain} volta a chamar este metodo para preencher
     * o campo {@code project}, e reusa-lo aqui daria recursao infinita. Passando a
     * obra ja construida para os filhos, o ciclo se fecha numa volta so.
     */
    public static Project toDomain(ProjectJpa jpa) {
        if (jpa == null) return null;

        Project project = Project.restore(
                jpa.getId(),
                jpa.getOs(),
                jpa.getServiceProvided(),
                ClientMapper.toDomain(jpa.getClient()),
                null,
                null,
                null,
                null,
                jpa.getStartDate(),
                jpa.getEndDate(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );

        for (EmployeeJpa employee : jpa.getEmployees()) {
            project.addEmployee(EmployeeMapper.toDomain(employee));
        }
        for (EquipmentJpa equipment : jpa.getEquipments()) {
            project.addEquipment(EquipmentMapper.toDomain(equipment));
        }
        for (RestaurantJpa restaurant : jpa.getRestaurants()) {
            project.addRestaurant(restaurantOf(restaurant, project));
        }
        for (AccommodationJpa accommodation : jpa.getAccommodations()) {
            project.addAccommodation(accommodationOf(accommodation, project));
        }
        return project;
    }

    private static Restaurant restaurantOf(RestaurantJpa jpa, Project project) {
        return Restaurant.restore(
                jpa.getId(),
                jpa.getName(),
                jpa.getEmail(),
                jpa.getPhone(),
                jpa.getCnpj(),
                project,
                null,
                jpa.getIsBilled(),
                jpa.getLunchPrice(),
                jpa.getDinnerPrice(),
                jpa.getTotal(),
                jpa.getAdditionalValues(),
                jpa.getValuePerEmployee(),
                jpa.getDays(),
                AddressMapper.toDomain(jpa.getAddress()),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }

    private static Accommodation accommodationOf(AccommodationJpa jpa, Project project) {
        return Accommodation.restore(
                jpa.getId(),
                AddressMapper.toDomain(jpa.getAddress()),
                project,
                jpa.getCapacity(),
                jpa.getStartDate(),
                jpa.getEndDate(),
                null,
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
