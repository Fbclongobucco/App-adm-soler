package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccommodationTest {

    @Test
    void rescheduleMovesBothDatesForward() {
        Accommodation accommodation = DomainFixture.accommodation();

        accommodation.reschedule(DomainFixture.MAR, DomainFixture.ABR);

        assertThat(accommodation.getStartDate()).isEqualTo(DomainFixture.MAR);
        assertThat(accommodation.getEndDate()).isEqualTo(DomainFixture.ABR);
    }

    @Test
    void rescheduleRejectsInvertedRange() {
        Accommodation accommodation = DomainFixture.accommodation();

        assertThatThrownBy(() -> accommodation.reschedule(DomainFixture.ABR, DomainFixture.MAR))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void setCapacityAcceptsNull() {
        Accommodation accommodation = DomainFixture.accommodation();

        accommodation.setCapacity(null);

        assertThat(accommodation.getCapacity()).isNull();
    }

    @Test
    void setCapacityRejectsZero() {
        Accommodation accommodation = DomainFixture.accommodation();

        assertThatThrownBy(() -> accommodation.setCapacity(0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("capacity must be greater than zero");

        assertThat(accommodation.getCapacity()).isEqualTo(4);
    }
}
