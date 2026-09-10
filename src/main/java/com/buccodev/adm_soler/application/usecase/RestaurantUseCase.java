package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequestDto;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponseDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.application.exception.RestaurantNotFoundException;
import com.buccodev.adm_soler.application.mapper.PageMapper;
import com.buccodev.adm_soler.application.mapper.RestaurantMapper;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;

import java.util.UUID;

public class RestaurantUseCase {

    /**
     * Ainda nao existe alocacao de funcionario a restaurante na API, entao o rateio
     * sai zerado. Quando o vinculo existir, a contagem vem do repositorio.
     */
    private static final int NO_EMPLOYEES_ASSIGNED = 0;

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final ProjectRepository projectRepository;

    public RestaurantUseCase(RestaurantRepository restaurantRepository,
                             AddressRepository addressRepository,
                             ProjectRepository projectRepository) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.projectRepository = projectRepository;
    }

    public RestaurantResponseDto createRestaurant(RestaurantRequestDto request) {
        requireAddress(request.addressId());
        requireProject(request.projectId());
        Restaurant saved = restaurantRepository.save(RestaurantMapper.toDomain(request));
        return toResponseDto(saved);
    }

    public RestaurantResponseDto getRestaurantById(UUID id) {
        return toResponseDto(findRestaurant(id));
    }

    public PageResponseDto<RestaurantResponseDto> listRestaurants(int page, int size) {
        var result = restaurantRepository.findAll(new Repository.PageQuery(page, size));
        return PageMapper.toResponseDto(result, this::toResponseDto);
    }

    public RestaurantResponseDto updateRestaurant(UUID id, RestaurantRequestDto request) {
        Restaurant restaurant = findRestaurant(id);
        requireAddress(request.addressId());
        requireProject(request.projectId());
        restaurant.update(request.name(), request.email(), request.phone(), request.cnpj(),
                request.projectId(), request.addressId(), request.isBilled(), request.lunchPrice(),
                request.dinnerPrice(), request.additionalValues(), request.days());
        return toResponseDto(restaurantRepository.save(restaurant));
    }

    public void deleteRestaurant(UUID id) {
        restaurantRepository.delete(findRestaurant(id));
    }

    private RestaurantResponseDto toResponseDto(Restaurant restaurant) {
        return RestaurantMapper.toResponseDto(restaurant, NO_EMPLOYEES_ASSIGNED);
    }

    private Restaurant findRestaurant(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> RestaurantNotFoundException.withId(id));
    }

    private void requireAddress(UUID addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw AddressNotFoundException.withId(addressId);
        }
    }

    private void requireProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException.withId(projectId);
        }
    }
}
