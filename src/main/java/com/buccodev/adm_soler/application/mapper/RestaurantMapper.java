package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;

import java.util.Collections;
import java.util.stream.Collectors;

public class RestaurantMapper {

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

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getEmail(),
                restaurant.getPhone(),
                restaurant.getCnpj(),
                restaurant.getProject() != null ? restaurant.getProject().getId() : null,
                restaurant.getEmployees() != null
                        ? restaurant.getEmployees().stream().map(e -> e.getId()).collect(Collectors.toSet())
                        : Collections.emptySet(),
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
}
