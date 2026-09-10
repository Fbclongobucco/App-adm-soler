package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequestDto;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponseDto;
import com.buccodev.adm_soler.core.domain.Restaurant;

public final class RestaurantMapper {

    private RestaurantMapper() {
    }

    public static Restaurant toDomain(RestaurantRequestDto dto) {
        return Restaurant.create(dto.name(), dto.email(), dto.phone(), dto.cnpj(), dto.projectId(),
                dto.addressId(), dto.isBilled(), dto.lunchPrice(), dto.dinnerPrice(),
                dto.additionalValues(), dto.days());
    }

    /**
     * O rateio por funcionario depende de quantos funcionarios o restaurante atende,
     * informacao de outro agregado — por isso a contagem entra como parametro.
     */
    public static RestaurantResponseDto toResponseDto(Restaurant restaurant, int employeeCount) {
        return new RestaurantResponseDto(restaurant.getId(), restaurant.getName(),
                restaurant.getEmail(), restaurant.getPhone(), restaurant.getCnpj(),
                restaurant.getProjectId(), restaurant.getAddressId(), restaurant.getIsBilled(),
                restaurant.getLunchPrice(), restaurant.getDinnerPrice(),
                restaurant.getAdditionalValues(), restaurant.getDays(), restaurant.getTotal(),
                restaurant.valuePerEmployee(employeeCount),
                restaurant.getCreatedAt(), restaurant.getUpdatedAt());
    }
}
