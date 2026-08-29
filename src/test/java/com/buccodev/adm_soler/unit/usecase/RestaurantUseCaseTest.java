package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequest;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.RestaurantUseCase;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.domain.Restaurant;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private Address sampleAddress;
    private Project sampleProject;
    private Restaurant sampleRestaurant;

    @BeforeEach
    void setUp() {
        sampleAddress = Address.restore(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
        Client sampleClient = Client.restore(
                UUID.randomUUID(), "Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress, Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleProject = Project.restore(
                UUID.randomUUID(), "OS-001", "Servico X", sampleClient,
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleRestaurant = Restaurant.restore(
                UUID.randomUUID(), "Restaurante Teste", "rest@email.com", "1234567890",
                "12.345.678/0001-99", sampleProject, Collections.emptySet(), true,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                5, sampleAddress, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateRestaurant() {
        RestaurantRequest request = new RestaurantRequest("Restaurante Teste", "rest@email.com", "1234567890",
                "12.345.678/0001-99", sampleProject.getId(), true, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ZERO, 5, sampleAddress.getId());
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(sampleRestaurant);

        RestaurantResponse response = restaurantUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Restaurante Teste");
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentAddress() {
        UUID addressId = UUID.randomUUID();
        RestaurantRequest request = new RestaurantRequest("Restaurante Teste", "rest@email.com", "1234567890",
                "12.345.678/0001-99", sampleProject.getId(), true, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ZERO, 5, addressId);
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentProject() {
        UUID projectId = UUID.randomUUID();
        RestaurantRequest request = new RestaurantRequest("Restaurante Teste", "rest@email.com", "1234567890",
                "12.345.678/0001-99", projectId, true, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ZERO, 5, sampleAddress.getId());
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindById() {
        when(restaurantRepository.findById(sampleRestaurant.getId())).thenReturn(Optional.of(sampleRestaurant));

        RestaurantResponse response = restaurantUseCase.findById(sampleRestaurant.getId());

        assertThat(response.name()).isEqualTo("Restaurante Teste");
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(restaurantRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleRestaurant), 0, 20, 1, 1));

        PageResponse<RestaurantResponse> responses = restaurantUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        RestaurantRequest request = new RestaurantRequest("Restaurante Atualizado", "rest@email.com", "1234567890",
                "12.345.678/0001-99", sampleProject.getId(), false, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ZERO, 3, sampleAddress.getId());
        Restaurant updated = Restaurant.restore(
                sampleRestaurant.getId(), "Restaurante Atualizado", "rest@email.com", "1234567890",
                "12.345.678/0001-99", sampleProject, Collections.emptySet(), false,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                3, sampleAddress, sampleRestaurant.getCreatedAt(), LocalDateTime.now()
        );
        when(restaurantRepository.findById(sampleRestaurant.getId())).thenReturn(Optional.of(sampleRestaurant));
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updated);

        RestaurantResponse response = restaurantUseCase.update(sampleRestaurant.getId(), request);

        assertThat(response.name()).isEqualTo("Restaurante Atualizado");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        RestaurantRequest request = new RestaurantRequest("Restaurante", "rest@email.com", "1234567890",
                "12.345.678/0001-99", sampleProject.getId(), true, BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ZERO, 5, sampleAddress.getId());
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantUseCase.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        when(restaurantRepository.existsById(sampleRestaurant.getId())).thenReturn(true);
        doNothing().when(restaurantRepository).deleteById(sampleRestaurant.getId());

        restaurantUseCase.delete(sampleRestaurant.getId());

        verify(restaurantRepository).deleteById(sampleRestaurant.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> restaurantUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
