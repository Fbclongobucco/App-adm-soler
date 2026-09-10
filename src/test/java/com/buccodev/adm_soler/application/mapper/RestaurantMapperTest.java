package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.restaurant.RestaurantRequestDto;
import com.buccodev.adm_soler.core.domain.Restaurant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantMapperTest {

    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final UUID ADDRESS_ID = UUID.randomUUID();

    private static Restaurant restaurant() {
        return Restaurant.create("Restaurante do Ze", null, null, null, PROJECT_ID, ADDRESS_ID,
                false, new BigDecimal("20.00"), new BigDecimal("25.00"),
                new BigDecimal("100.00"), 10);
    }

    @Test
    void toDomainBuildsANewRestaurant() {
        Restaurant built = RestaurantMapper.toDomain(new RestaurantRequestDto("Cantina", null, null,
                null, PROJECT_ID, ADDRESS_ID, true, new BigDecimal("10.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, 5));

        assertEquals("Cantina", built.getName());
        assertTrue(built.isBilled());
        assertEquals(0, new BigDecimal("50.00").compareTo(built.getTotal()));
    }

    @Test
    void toResponseDtoSplitsTheTotalByTheGivenEmployeeCount() {
        var dto = RestaurantMapper.toResponseDto(restaurant(), 10);

        assertEquals(0, new BigDecimal("550.00").compareTo(dto.total()));
        assertEquals(0, new BigDecimal("55.00").compareTo(dto.valuePerEmployee()));
    }

    @Test
    void toResponseDtoReportsZeroWhenNobodyIsAssigned() {
        var dto = RestaurantMapper.toResponseDto(restaurant(), 0);

        assertEquals(0, BigDecimal.ZERO.compareTo(dto.valuePerEmployee()));
    }
}
