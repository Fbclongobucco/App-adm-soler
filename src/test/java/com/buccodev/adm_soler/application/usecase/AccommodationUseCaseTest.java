package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequestDto;
import com.buccodev.adm_soler.application.exception.AccommodationNotFoundException;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ProjectNotFoundException;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccommodationUseCaseTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProjectRepository projectRepository;

    private AccommodationUseCase accommodationUseCase;
    private UUID addressId;
    private UUID projectId;
    private Accommodation sampleAccommodation;

    private static final LocalDateTime START = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 2, 1, 12, 0);

    @BeforeEach
    void setUp() {
        accommodationUseCase = new AccommodationUseCase(accommodationRepository, addressRepository,
                projectRepository);
        addressId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        sampleAccommodation = Accommodation.create(addressId, projectId, 4, START, END);
    }

    private AccommodationRequestDto request(Integer capacity) {
        return new AccommodationRequestDto(addressId, projectId, capacity, START, END);
    }

    @Test
    void createAccommodationSavesAndReturnsIt() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(accommodationRepository.save(any(Accommodation.class))).thenAnswer(i -> i.getArgument(0));

        var response = accommodationUseCase.createAccommodation(request(4));

        assertEquals(4, response.capacity());
        assertEquals(projectId, response.projectId());
    }

    @Test
    void createAccommodationThrowsWhenAddressIsMissing() {
        when(addressRepository.existsById(addressId)).thenReturn(false);

        assertThrows(AddressNotFoundException.class,
                () -> accommodationUseCase.createAccommodation(request(4)));
    }

    @Test
    void createAccommodationThrowsWhenProjectIsMissing() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class,
                () -> accommodationUseCase.createAccommodation(request(4)));
        verify(accommodationRepository, never()).save(any());
    }

    @Test
    void getAccommodationByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccommodationNotFoundException.class,
                () -> accommodationUseCase.getAccommodationById(id));
    }

    @Test
    void listAccommodationsMapsThePage() {
        when(accommodationRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleAccommodation), 0, 20, 1, 1));

        assertEquals(1, accommodationUseCase.listAccommodations(0, 20).content().size());
    }

    @Test
    void updateAccommodationAppliesTheChange() {
        when(accommodationRepository.findById(sampleAccommodation.getId()))
                .thenReturn(Optional.of(sampleAccommodation));
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(accommodationRepository.save(any(Accommodation.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(8, accommodationUseCase.updateAccommodation(sampleAccommodation.getId(),
                request(8)).capacity());
    }

    @Test
    void deleteAccommodationRemovesTheEntity() {
        when(accommodationRepository.findById(sampleAccommodation.getId()))
                .thenReturn(Optional.of(sampleAccommodation));

        accommodationUseCase.deleteAccommodation(sampleAccommodation.getId());

        verify(accommodationRepository).delete(sampleAccommodation);
    }
}
