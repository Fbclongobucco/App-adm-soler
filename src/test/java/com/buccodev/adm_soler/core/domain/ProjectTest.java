package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTest {

    @Test
    void rescheduleMovesBothDatesForward() {
        Project project = DomainFixture.project(DomainFixture.JAN, DomainFixture.FEV);

        project.reschedule(DomainFixture.MAR, DomainFixture.ABR);

        assertThat(project.getStartDate()).isEqualTo(DomainFixture.MAR);
        assertThat(project.getEndDate()).isEqualTo(DomainFixture.ABR);
    }

    @Test
    void rescheduleRejectsInvertedRange() {
        Project project = DomainFixture.project(DomainFixture.JAN, DomainFixture.FEV);

        assertThatThrownBy(() -> project.reschedule(DomainFixture.ABR, DomainFixture.MAR))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("startDate must be before endDate");
    }

    @Test
    void rescheduleKeepsPreviousDatesWhenRejected() {
        Project project = DomainFixture.project(DomainFixture.JAN, DomainFixture.FEV);

        assertThatThrownBy(() -> project.reschedule(DomainFixture.ABR, DomainFixture.MAR))
                .isInstanceOf(BadRequestException.class);

        assertThat(project.getStartDate()).isEqualTo(DomainFixture.JAN);
        assertThat(project.getEndDate()).isEqualTo(DomainFixture.FEV);
    }
}
