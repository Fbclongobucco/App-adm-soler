package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
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
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + request.projectId()));
        Restaurant restaurant = request.toDomain(project, address);
        Restaurant saved = restaurantRepository.save(restaurant);
        return RestaurantResponse.fromDomain(saved);
    }

    public RestaurantResponse findById(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante nao encontrado com id: " + id));
        return RestaurantResponse.fromDomain(restaurant);
    }

    public List<RestaurantResponse> findAll() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantResponse::fromDomain)
                .toList();
    }

    public RestaurantResponse update(UUID id, RestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante nao encontrado com id: " + id));
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado com id: " + request.addressId()));
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado com id: " + request.projectId()));
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
        Restaurant updated = restaurantRepository.save(restaurant);
        return RestaurantResponse.fromDomain(updated);
    }

    public void delete(UUID id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurante nao encontrado com id: " + id);
        }
        restaurantRepository.deleteById(id);
    }
}
