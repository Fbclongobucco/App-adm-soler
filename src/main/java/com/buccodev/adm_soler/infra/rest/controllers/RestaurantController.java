package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequestDto;
import com.buccodev.adm_soler.application.dto.restaurant.RestaurantResponseDto;
import com.buccodev.adm_soler.application.usecase.RestaurantUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Restaurants", description = "Restaurantes que atendem uma obra, com o custo de refeicoes")
@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantUseCase restaurantUseCase;

    public RestaurantController(RestaurantUseCase restaurantUseCase) {
        this.restaurantUseCase = restaurantUseCase;
    }

    @Operation(summary = "Cria um restaurante", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> create(@Valid @RequestBody RestaurantRequestDto request) {
        RestaurantResponseDto created = restaurantUseCase.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um restaurante por id")
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(restaurantUseCase.getRestaurantById(id));
    }

    @Operation(summary = "Lista restaurantes paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<RestaurantResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(restaurantUseCase.listRestaurants(page, size));
    }

    @Operation(summary = "Atualiza um restaurante", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody RestaurantRequestDto request) {
        return ResponseEntity.ok(restaurantUseCase.updateRestaurant(id, request));
    }

    @Operation(summary = "Remove um restaurante", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        restaurantUseCase.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
