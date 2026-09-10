package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidEmployeeException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private static final UUID ADDRESS_ID = UUID.randomUUID();

    private static Employee valid() {
        return Employee.create("Maria Souza", "maria@soler.com", "11987654321", ADDRESS_ID, "PINTOR");
    }

    @Test
    void createsEmployeeWithValidData() {
        Employee employee = valid();

        assertNotNull(employee.getId());
        assertEquals("PINTOR", employee.getRole());
        assertEquals(ADDRESS_ID, employee.getAddressId());
    }

    @Test
    void throwsWhenNameIsBlank() {
        assertThrows(InvalidEmployeeException.class,
                () -> Employee.create(" ", null, null, ADDRESS_ID, "PINTOR"));
    }

    @Test
    void throwsWhenRoleIsBlank() {
        assertThrows(InvalidEmployeeException.class,
                () -> Employee.create("Maria", null, null, ADDRESS_ID, ""));
    }

    @Test
    void throwsWhenAddressIdIsNull() {
        assertThrows(InvalidEmployeeException.class,
                () -> Employee.create("Maria", null, null, null, "PINTOR"));
    }

    @Test
    void throwsWhenEmailIsMalformed() {
        assertThrows(InvalidEmployeeException.class,
                () -> Employee.create("Maria", "arroba-faltando", null, ADDRESS_ID, "PINTOR"));
    }

    @Test
    void updateReplacesFields() {
        Employee employee = valid();

        employee.update("Maria S. Souza", null, null, ADDRESS_ID, "ALPINISTA INDUSTRIAL");

        assertEquals("Maria S. Souza", employee.getName());
        assertEquals("ALPINISTA INDUSTRIAL", employee.getRole());
        assertNull(employee.getEmail());
    }
}
