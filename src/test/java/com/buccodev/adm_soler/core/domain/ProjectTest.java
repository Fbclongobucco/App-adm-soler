package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidPeriodException;
import com.buccodev.adm_soler.core.exception.InvalidProjectException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 8, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 3, 31, 18, 0);

    private static Project valid() {
        return Project.create("OS-1024", "Retrofit de cobertura", CLIENT_ID, START, END);
    }

    @Test
    void createsProjectWithValidData() {
        Project project = valid();

        assertNotNull(project.getId());
        assertEquals("OS-1024", project.getOs());
        assertEquals(CLIENT_ID, project.getClientId());
    }

    @Test
    void throwsWhenOsIsBlank() {
        assertThrows(InvalidProjectException.class,
                () -> Project.create("", "Retrofit", CLIENT_ID, START, END));
    }

    @Test
    void throwsWhenServiceProvidedIsBlank() {
        assertThrows(InvalidProjectException.class,
                () -> Project.create("OS-1", " ", CLIENT_ID, START, END));
    }

    @Test
    void throwsWhenClientIdIsNull() {
        assertThrows(InvalidProjectException.class,
                () -> Project.create("OS-1", "Retrofit", null, START, END));
    }

    @Test
    void throwsWhenPeriodBoundIsNull() {
        assertThrows(InvalidPeriodException.class,
                () -> Project.create("OS-1", "Retrofit", CLIENT_ID, START, null));
    }

    @Test
    void throwsWhenStartIsAfterEnd() {
        assertThrows(InvalidPeriodException.class,
                () -> Project.create("OS-1", "Retrofit", CLIENT_ID, END, START));
    }

    @Test
    void updateRejectsInvertedPeriodAndKeepsPreviousState() {
        Project project = valid();

        assertThrows(InvalidPeriodException.class,
                () -> project.update("OS-1024", "Retrofit", CLIENT_ID, END, START));
        assertEquals(START, project.getStartDate());
    }

    @Test
    void updateReplacesFields() {
        Project project = valid();
        UUID otherClient = UUID.randomUUID();

        project.update("OS-2048", "Manutencao preventiva", otherClient, START, END);

        assertEquals("OS-2048", project.getOs());
        assertEquals(otherClient, project.getClientId());
    }
}
