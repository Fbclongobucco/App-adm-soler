package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequestDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.application.exception.RestaurantNotFoundException;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProjectRepository projectRepository;

    private RestaurantUseCase restaurantUseCase;
    private UUID projectId;
    private UUID addressId;
    private Restaurant sampleRestaurant;

    @BeforeEach
    void setUp() {
        restaurantUseCase = new RestaurantUseCase(restaurantRepository, addressRepository,
                projectRepository);
        projectId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        sampleRestaurant = Restaurant.create("Restaurante do Ze", null, null, null, projectId,
                addressId, false, new BigDecimal("20.00"), new BigDecimal("25.00"),
                new BigDecimal("100.00"), 10);
    }

    private RestaurantRequestDto request(String name) {
        return new RestaurantRequestDto(name, null, null, null, projectId, addressId, false,
                new BigDecimal("20.00"), new BigDecimal("25.00"), new BigDecimal("100.00"), 10);
    }

    @Test
    void createRestaurantSavesAndReturnsItWithDerivedTotal() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(i -> i.getArgument(0));

        var response = restaurantUseCase.createRestaurant(request("Restaurante do Ze"));

        assertEquals(0, new BigDecimal("550.00").compareTo(response.total()));
    }

    @Test
    void valuePerEmployeeIsZeroWhileThereIsNoAssignment() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(i -> i.getArgument(0));

        var response = restaurantUseCase.createRestaurant(request("Restaurante do Ze"));

        assertEquals(0, BigDecimal.ZERO.compareTo(response.valuePerEmployee()));
    }

    @Test
    void createRestaurantThrowsWhenProjectIsMissing() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class,
                () -> restaurantUseCase.createRestaurant(request("Restaurante do Ze")));
    }

    @Test
    void createRestaurantThrowsWhenAddressIsMissing() {
        when(addressRepository.existsById(addressId)).thenReturn(false);

        assertThrows(AddressNotFoundException.class,
                () -> restaurantUseCase.createRestaurant(request("Restaurante do Ze")));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void getRestaurantByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> restaurantUseCase.getRestaurantById(id));
    }

    @Test
    void listRestaurantsMapsThePage() {
        when(restaurantRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleRestaurant), 0, 20, 1, 1));

        assertEquals(1, restaurantUseCase.listRestaurants(0, 20).content().size());
    }

    @Test
    void updateRestaurantAppliesTheChange() {
        when(restaurantRepository.findById(sampleRestaurant.getId()))
                .thenReturn(Optional.of(sampleRestaurant));
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Cantina Nova", restaurantUseCase.updateRestaurant(sampleRestaurant.getId(),
                request("Cantina Nova")).name());
    }

    @Test
    void deleteRestaurantRemovesTheEntity() {
        when(restaurantRepository.findById(sampleRestaurant.getId()))
                .thenReturn(Optional.of(sampleRestaurant));

        restaurantUseCase.deleteRestaurant(sampleRestaurant.getId());

        verify(restaurantRepository).delete(sampleRestaurant);
    }
}
