package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationRequest;
import com.buccodev.adm_soler.application.dto.accommodation.AccommodationResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.AccommodationUseCase;
import com.buccodev.adm_soler.core.domain.Accommodation;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.domain.Project;
import com.buccodev.adm_soler.core.repository.AccommodationRepository;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import com.buccodev.adm_soler.core.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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

    @InjectMocks
    private AccommodationUseCase accommodationUseCase;

    private Address sampleAddress;
    private Project sampleProject;
    private Accommodation sampleAccommodation;

    @BeforeEach
    void setUp() {
        sampleAddress = Address.restore(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
        Client sampleClient = Client.restore(
                UUID.randomUUID(), "Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress, Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleProject = Project.restore(
                UUID.randomUUID(), "OS-001", "Servico X", sampleClient,
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleAccommodation = Accommodation.restore(
                UUID.randomUUID(), sampleAddress, sampleProject, 10,
                LocalDateTime.now(), LocalDateTime.now().plusDays(10),
                Collections.emptySet(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateAccommodation() {
        AccommodationRequest request = new AccommodationRequest(sampleAddress.getId(), sampleProject.getId(),
                10, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(projectRepository.findById(sampleProject.getId())).thenReturn(Optional.of(sampleProject));
        when(accommodationRepository.save(any(Accommodation.class))).thenReturn(sampleAccommodation);

        AccommodationResponse response = accommodationUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.capacity()).isEqualTo(10);
        verify(accommodationRepository).save(any(Accommodation.class));
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentAddress() {
        UUID addressId = UUID.randomUUID();
        AccommodationRequest request = new AccommodationRequest(addressId, sampleProject.getId(),
                10, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentProject() {
        UUID projectId = UUID.randomUUID();
        AccommodationRequest request = new AccommodationRequest(sampleAddress.getId(), projectId,
                10, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindById() {
        when(accommodationRepository.findById(sampleAccommodation.getId())).thenReturn(Optional.of(sampleAccommodation));

        AccommodationResponse response = accommodationUseCase.findById(sampleAccommodation.getId());

        assertThat(response.capacity()).isEqualTo(10);
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(accommodationRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleAccommodation), 0, 20, 1, 1));

        PageResponse<AccommodationResponse> responses = accommodationUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        AccommodationRequest request = new AccommodationRequest(sampleAddress.getId(), sampleProject.getId(),
                20, LocalDateTime.now(), LocalDateTime.now().plusDays(20));
        Accommodation updated = Accommodation.restore(
                sampleAccommodation.getId(), sampleAddress, sampleProject, 20,
                LocalDateTime.now(), LocalDateTime.now().plusDays(20),
                Collections.emptySet(), sampleAccommodation.getCreatedAt(), LocalDateTime.now()
        );
        when(accommodationRepository.findById(sampleAccommodation.getId())).thenReturn(Optional.of(sampleAccommodation));
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(accommodationRepository.save(any(Accommodation.class))).thenReturn(updated);

        AccommodationResponse response = accommodationUseCase.update(sampleAccommodation.getId(), request);

        assertThat(response.capacity()).isEqualTo(20);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        AccommodationRequest request = new AccommodationRequest(sampleAddress.getId(), sampleProject.getId(),
                10, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationUseCase.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        when(accommodationRepository.existsById(sampleAccommodation.getId())).thenReturn(true);
        doNothing().when(accommodationRepository).deleteById(sampleAccommodation.getId());

        accommodationUseCase.delete(sampleAccommodation.getId());

        verify(accommodationRepository).deleteById(sampleAccommodation.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(accommodationRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> accommodationUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
