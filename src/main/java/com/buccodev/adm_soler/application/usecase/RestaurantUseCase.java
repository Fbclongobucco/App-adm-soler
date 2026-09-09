package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.PageResponseMapper;
import com.buccodev.adm_soler.application.mapper.RestaurantDtoMapper;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.pagination.PageQuery;
import com.buccodev.adm_soler.core.pagination.PageResult;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;

import java.util.UUID;

public class RestaurantUseCase {

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

    public RestaurantResponse create(RestaurantRequest request) {
        Address address = findAddress(request.addressId());
        Project project = findProject(request.projectId());
        Restaurant saved = restaurantRepository.save(
                RestaurantDtoMapper.toDomain(request, project, address));
        return RestaurantDtoMapper.toResponse(saved);
    }

    public RestaurantResponse findById(UUID id) {
        return RestaurantDtoMapper.toResponse(findRestaurant(id));
    }

    public PageResponse<RestaurantResponse> findAll(int page, int size) {
        PageResult<Restaurant> result = restaurantRepository.findAll(new PageQuery(page, size));
        return PageResponseMapper.toResponse(result, RestaurantDtoMapper::toResponse);
    }

    public RestaurantResponse update(UUID id, RestaurantRequest request) {
        Restaurant restaurant = findRestaurant(id);
        Address address = findAddress(request.addressId());
        Project project = findProject(request.projectId());
        RestaurantDtoMapper.applyTo(restaurant, request, project, address);
        return RestaurantDtoMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public void delete(UUID id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurante nao encontrado com id: " + id);
        }
        restaurantRepository.deleteById(id);
    }

    private Restaurant findRestaurant(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante nao encontrado com id: " + id));
    }

    private Address findAddress(UUID addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + addressId));
    }

    private Project findProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + projectId));
    }
}
