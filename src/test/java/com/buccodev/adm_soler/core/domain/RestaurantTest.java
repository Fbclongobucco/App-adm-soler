package com.buccodev.adm_soler.core.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantTest {

    private static Restaurant restaurant(String cnpj, BigDecimal lunchPrice, BigDecimal dinnerPrice,
                                         BigDecimal additionalValues, Integer days) {
        return Restaurant.create("Restaurante Bom Prato", "contato@bomprato.com", "11988887777",
                cnpj, DomainFixture.project(), true,
                lunchPrice, dinnerPrice, additionalValues, days, DomainFixture.address());
    }

    @Test
    void createPreservesCnpjAndPrices() {
        Restaurant restaurant = restaurant("98.765.432/0001-11", new BigDecimal("30.00"),
                new BigDecimal("25.00"), new BigDecimal("100.00"), 20);

        assertThat(restaurant.getCnpj()).isEqualTo("98.765.432/0001-11");
        assertThat(restaurant.getLunchPrice()).isEqualByComparingTo("30.00");
        assertThat(restaurant.getDinnerPrice()).isEqualByComparingTo("25.00");
        assertThat(restaurant.getAdditionalValues()).isEqualByComparingTo("100.00");
    }

    @Test
    void createCalculatesTotalFromPricesAndDays() {
        Restaurant restaurant = restaurant("98.765.432/0001-11", new BigDecimal("30.00"),
                new BigDecimal("25.00"), new BigDecimal("100.00"), 20);

        // (30 + 25) * 20 dias + 100 de valores adicionais
        assertThat(restaurant.getTotal()).isEqualByComparingTo("1200.00");
    }

    @Test
    void createTreatsMissingPricesAsZero() {
        Restaurant restaurant = restaurant("98.765.432/0001-11", null, null, null, 10);

        assertThat(restaurant.getLunchPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(restaurant.getDinnerPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(restaurant.getAdditionalValues()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(restaurant.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createKeepsCnpjNullWhenNotInformed() {
        Restaurant restaurant = restaurant(null, new BigDecimal("30.00"),
                BigDecimal.ZERO, BigDecimal.ZERO, 10);

        assertThat(restaurant.getCnpj()).isNull();
    }
}
