package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidAccommodationException;
import com.buccodev.adm_soler.core.exception.InvalidPeriodException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccommodationTest {

    private static final UUID ADDRESS_ID = UUID.randomUUID();
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 2, 1, 12, 0);

    private static Accommodation valid() {
        return Accommodation.create(ADDRESS_ID, PROJECT_ID, 4, START, END);
    }

    @Test
    void createsAccommodationWithValidData() {
        Accommodation accommodation = valid();

        assertNotNull(accommodation.getId());
        assertEquals(4, accommodation.getCapacity());
        assertEquals(PROJECT_ID, accommodation.getProjectId());
    }

    @Test
    void throwsWhenAddressIdIsNull() {
        assertThrows(InvalidAccommodationException.class,
                () -> Accommodation.create(null, PROJECT_ID, 4, START, END));
    }

    @Test
    void throwsWhenProjectIdIsNull() {
        assertThrows(InvalidAccommodationException.class,
                () -> Accommodation.create(ADDRESS_ID, null, 4, START, END));
    }

    @Test
    void throwsWhenCapacityIsNotPositive() {
        assertThrows(InvalidAccommodationException.class,
                () -> Accommodation.create(ADDRESS_ID, PROJECT_ID, 0, START, END));
    }

    @Test
    void throwsWhenStartIsAfterEnd() {
        assertThrows(InvalidPeriodException.class,
                () -> Accommodation.create(ADDRESS_ID, PROJECT_ID, 4, END, START));
    }

    @Test
    void fitsRespectsCapacity() {
        Accommodation accommodation = valid();

        assertTrue(accommodation.fits(4));
        assertFalse(accommodation.fits(5));
    }

    @Test
    void fitsAnyCountWhenCapacityIsUnknown() {
        Accommodation accommodation = Accommodation.create(ADDRESS_ID, PROJECT_ID, null, START, END);

        assertTrue(accommodation.fits(999));
    }

    @Test
    void updateKeepsProjectFixed() {
        Accommodation accommodation = valid();
        UUID newAddress = UUID.randomUUID();

        accommodation.update(newAddress, 8, START, END);

        assertEquals(newAddress, accommodation.getAddressId());
        assertEquals(8, accommodation.getCapacity());
        assertEquals(PROJECT_ID, accommodation.getProjectId());
    }
}
