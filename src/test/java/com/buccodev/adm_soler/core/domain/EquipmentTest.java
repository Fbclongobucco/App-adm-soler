package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidEquipmentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentTest {

    @Test
    void createsEquipmentWithValidData() {
        Equipment equipment = Equipment.create("Andaime", "Andaime tubular 2m");

        assertNotNull(equipment.getId());
        assertEquals("Andaime", equipment.getName());
        assertEquals("Andaime tubular 2m", equipment.getDescription());
    }

    @Test
    void allowsNullDescription() {
        assertNull(Equipment.create("Talha", null).getDescription());
    }

    @Test
    void throwsWhenNameIsNull() {
        assertThrows(InvalidEquipmentException.class, () -> Equipment.create(null, "x"));
    }

    @Test
    void throwsWhenNameIsBlank() {
        assertThrows(InvalidEquipmentException.class, () -> Equipment.create("   ", "x"));
    }

    @Test
    void updateReplacesFields() {
        Equipment equipment = Equipment.create("Andaime", null);

        equipment.update("Linha de vida", "Cabo de aco 8mm");

        assertEquals("Linha de vida", equipment.getName());
        assertEquals("Cabo de aco 8mm", equipment.getDescription());
    }

    @Test
    void updateRejectsBlankName() {
        Equipment equipment = Equipment.create("Andaime", null);

        assertThrows(InvalidEquipmentException.class, () -> equipment.update("", null));
        assertEquals("Andaime", equipment.getName());
    }
}
