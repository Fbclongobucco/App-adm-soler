package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidAddressException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private static Address valid() {
        return Address.create("Rua das Obras", "100", "Galpao 2", "Centro",
                "Sao Paulo", "SP", "01000-000", "BR");
    }

    @Test
    void createsAddressWithValidData() {
        Address address = valid();

        assertNotNull(address.getId());
        assertEquals("Rua das Obras", address.getStreet());
        assertEquals("Sao Paulo", address.getCity());
        assertNotNull(address.getCreatedAt());
        assertEquals(address.getCreatedAt(), address.getUpdatedAt());
    }

    @Test
    void throwsWhenStreetIsBlank() {
        assertThrows(InvalidAddressException.class,
                () -> Address.create("  ", "1", null, null, "Sao Paulo", "SP", "01000-000", "BR"));
    }

    @Test
    void throwsWhenCityIsNull() {
        assertThrows(InvalidAddressException.class,
                () -> Address.create("Rua", "1", null, null, null, "SP", "01000-000", "BR"));
    }

    @Test
    void throwsWhenStateIsNull() {
        assertThrows(InvalidAddressException.class,
                () -> Address.create("Rua", "1", null, null, "Sao Paulo", null, "01000-000", "BR"));
    }

    @Test
    void throwsWhenZipCodeIsBlank() {
        assertThrows(InvalidAddressException.class,
                () -> Address.create("Rua", "1", null, null, "Sao Paulo", "SP", "", "BR"));
    }

    @Test
    void throwsWhenCountryIsNull() {
        assertThrows(InvalidAddressException.class,
                () -> Address.create("Rua", "1", null, null, "Sao Paulo", "SP", "01000-000", null));
    }

    @Test
    void updateReplacesFieldsAndTouchesUpdatedAt() {
        Address address = valid();

        address.update("Avenida Nova", "200", null, "Bairro", "Campinas", "SP", "13000-000", "BR");

        assertEquals("Avenida Nova", address.getStreet());
        assertEquals("Campinas", address.getCity());
        assertNull(address.getComplement());
        assertTrue(address.getUpdatedAt().isAfter(address.getCreatedAt())
                || address.getUpdatedAt().isEqual(address.getCreatedAt()));
    }

    @Test
    void updateRejectsInvalidDataAndKeepsPreviousState() {
        Address address = valid();

        assertThrows(InvalidAddressException.class,
                () -> address.update("", "1", null, null, "Sao Paulo", "SP", "01000-000", "BR"));
        assertEquals("Rua das Obras", address.getStreet());
    }

    @Test
    void equalsIsBasedOnId() {
        Address address = valid();
        Address restored = Address.restore(address.getId(), "Outra", null, null, null,
                "Rio", "RJ", "20000-000", "BR", address.getCreatedAt(), address.getUpdatedAt());

        assertEquals(address, restored);
        assertEquals(address.hashCode(), restored.hashCode());
    }
}
