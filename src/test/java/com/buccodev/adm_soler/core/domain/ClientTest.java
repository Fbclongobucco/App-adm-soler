package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidClientException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private static final UUID ADDRESS_ID = UUID.randomUUID();

    private static Client valid() {
        return Client.create("Construtora XYZ", "contato@xyz.com", "11987654321",
                "11.222.333/0001-44", ADDRESS_ID);
    }

    @Test
    void createsClientWithValidData() {
        Client client = valid();

        assertNotNull(client.getId());
        assertEquals("Construtora XYZ", client.getName());
        assertEquals(ADDRESS_ID, client.getAddressId());
    }

    @Test
    void throwsWhenNameIsBlank() {
        assertThrows(InvalidClientException.class,
                () -> Client.create("", "c@x.com", null, null, ADDRESS_ID));
    }

    @Test
    void throwsWhenEmailIsMalformed() {
        assertThrows(InvalidClientException.class,
                () -> Client.create("XYZ", "sem-arroba", null, null, ADDRESS_ID));
    }

    @Test
    void throwsWhenPhoneIsMalformed() {
        assertThrows(InvalidClientException.class,
                () -> Client.create("XYZ", null, "123", null, ADDRESS_ID));
    }

    @Test
    void throwsWhenCnpjIsMalformed() {
        assertThrows(InvalidClientException.class,
                () -> Client.create("XYZ", null, null, "11222333000144", ADDRESS_ID));
    }

    @Test
    void throwsWhenAddressIdIsNull() {
        assertThrows(InvalidClientException.class,
                () -> Client.create("XYZ", null, null, null, null));
    }

    @Test
    void acceptsNullOptionalFields() {
        Client client = Client.create("XYZ", null, null, null, ADDRESS_ID);

        assertNull(client.getEmail());
        assertNull(client.getPhone());
        assertNull(client.getCnpj());
    }

    @Test
    void updateReplacesFields() {
        Client client = valid();
        UUID newAddress = UUID.randomUUID();

        client.update("Construtora ABC", "abc@abc.com", "11912345678",
                "99.888.777/0001-11", newAddress);

        assertEquals("Construtora ABC", client.getName());
        assertEquals(newAddress, client.getAddressId());
    }
}
