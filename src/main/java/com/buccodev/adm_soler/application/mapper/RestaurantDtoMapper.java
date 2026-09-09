package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class RestaurantDtoMapper {

    private RestaurantDtoMapper() {
    }

    public static Restaurant toDomain(RestaurantRequest request, Project project, Address address) {
        return Restaurant.create(
                request.name(),
                request.email(),
                request.phone(),
                project,
                request.isBilled(),
                request.days(),
                address
        );
    }

    public static void applyTo(Restaurant restaurant, RestaurantRequest request, Project project, Address address) {
        restaurant.setName(request.name());
        restaurant.setEmail(request.email());
        restaurant.setPhone(request.phone());
        restaurant.setCnpj(request.cnpj());
        restaurant.setProject(project);
        restaurant.setIsBilled(request.isBilled());
        restaurant.setLunchPrice(request.lunchPrice());
        restaurant.setDinnerPrice(request.dinnerPrice());
        restaurant.setAdditionalValues(request.additionalValues());
        restaurant.setDays(request.days());
        restaurant.setAddress(address);
    }

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getEmail(),
                restaurant.getPhone(),
                restaurant.getCnpj(),
                restaurant.getProject() != null ? restaurant.getProject().getId() : null,
                employeeIds(restaurant.getEmployees()),
                restaurant.getIsBilled(),
                restaurant.getLunchPrice(),
                restaurant.getDinnerPrice(),
                restaurant.getTotal(),
                restaurant.getAdditionalValues(),
                restaurant.getValuePerEmployee(),
                restaurant.getDays(),
                restaurant.getAddress() != null ? restaurant.getAddress().getId() : null,
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }

    private static Set<UUID> employeeIds(Set<Employee> employees) {
        if (employees == null) {
            return Collections.emptySet();
        }
        return employees.stream().map(Employee::getId).collect(Collectors.toSet());
    }
}
