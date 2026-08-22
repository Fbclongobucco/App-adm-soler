package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.mapper.RestaurantMapper;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;

import java.util.List;
import java.util.UUID;

public class RestaurantUseCase {

    private final RestaurantRepository restaurantRepository;
    private final ProjectRepository projectRepository;
    private final AddressRepository addressRepository;

    public RestaurantUseCase(RestaurantRepository restaurantRepository, ProjectRepository projectRepository,
                             AddressRepository addressRepository) {
        this.restaurantRepository = restaurantRepository;
        this.projectRepository = projectRepository;
        this.addressRepository = addressRepository;
    }

    public RestaurantResponse create(RestaurantRequest request) {
        var project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var restaurant = RestaurantMapper.toDomain(request, project, address);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse findById(UUID id) {
        var restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        return RestaurantMapper.toResponse(restaurant);
    }

    public List<RestaurantResponse> findAll() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }

    public RestaurantResponse update(UUID id, RestaurantRequest request) {
        findById(id);
        var project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        var address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        var restaurant = RestaurantMapper.toDomain(request, project, address);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public void delete(UUID id) {
        findById(id);
        restaurantRepository.deleteById(id);
    }
}
