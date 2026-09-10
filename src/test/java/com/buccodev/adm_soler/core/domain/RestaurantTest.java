package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidRestaurantException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final UUID ADDRESS_ID = UUID.randomUUID();

    private static Restaurant valid() {
        return Restaurant.create("Restaurante do Ze", "ze@rest.com", "11987654321",
                "11.222.333/0001-44", PROJECT_ID, ADDRESS_ID, false,
                new BigDecimal("20.00"), new BigDecimal("25.00"), new BigDecimal("100.00"), 10);
    }

    @Test
    void createsRestaurantWithValidData() {
        Restaurant restaurant = valid();

        assertNotNull(restaurant.getId());
        assertEquals(PROJECT_ID, restaurant.getProjectId());
        assertFalse(restaurant.isBilled());
    }

    @Test
    void totalIsDerivedFromPricesDaysAndAdditionalValues() {
        Restaurant restaurant = valid();

        assertEquals(0, new BigDecimal("550.00").compareTo(restaurant.getTotal()));
    }

    @Test
    void totalIsZeroWhenPricesAreMissing() {
        Restaurant restaurant = Restaurant.create("Sem preco", null, null, null,
                PROJECT_ID, ADDRESS_ID, null, null, null, null, null);

        assertEquals(0, BigDecimal.ZERO.compareTo(restaurant.getTotal()));
    }

    @Test
    void valuePerEmployeeSplitsTheTotal() {
        Restaurant restaurant = valid();

        assertEquals(0, new BigDecimal("55.00").compareTo(restaurant.valuePerEmployee(10)));
    }

    @Test
    void valuePerEmployeeIsZeroWhenNobodyIsAssigned() {
        assertEquals(0, BigDecimal.ZERO.compareTo(valid().valuePerEmployee(0)));
    }

    @Test
    void valuePerEmployeeRejectsNegativeCount() {
        Restaurant restaurant = valid();

        assertThrows(InvalidRestaurantException.class, () -> restaurant.valuePerEmployee(-1));
    }

    @Test
    void throwsWhenNameIsBlank() {
        assertThrows(InvalidRestaurantException.class,
                () -> Restaurant.create(" ", null, null, null, PROJECT_ID, ADDRESS_ID,
                        null, null, null, null, null));
    }

    @Test
    void throwsWhenPriceIsNegative() {
        assertThrows(InvalidRestaurantException.class,
                () -> Restaurant.create("Rest", null, null, null, PROJECT_ID, ADDRESS_ID,
                        null, new BigDecimal("-1.00"), null, null, null));
    }

    @Test
    void throwsWhenDaysIsNotPositive() {
        assertThrows(InvalidRestaurantException.class,
                () -> Restaurant.create("Rest", null, null, null, PROJECT_ID, ADDRESS_ID,
                        null, null, null, null, 0));
    }

    @Test
    void throwsWhenCnpjIsMalformed() {
        assertThrows(InvalidRestaurantException.class,
                () -> Restaurant.create("Rest", null, null, "123", PROJECT_ID, ADDRESS_ID,
                        null, null, null, null, null));
    }

    @Test
    void updateRecalculatesTotal() {
        Restaurant restaurant = valid();

        restaurant.update("Restaurante do Ze", null, null, null, PROJECT_ID, ADDRESS_ID, true,
                new BigDecimal("10.00"), BigDecimal.ZERO, BigDecimal.ZERO, 5);

        assertEquals(0, new BigDecimal("50.00").compareTo(restaurant.getTotal()));
        assertTrue(restaurant.isBilled());
    }
}
