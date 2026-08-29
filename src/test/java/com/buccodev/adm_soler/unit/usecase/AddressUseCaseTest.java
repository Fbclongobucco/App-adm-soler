package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.address.AddressRequest;
import com.buccodev.adm_soler.application.dto.address.AddressResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.AddressUseCase;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressUseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressUseCase addressUseCase;

    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        sampleAddress = Address.restore(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateAddress() {
        AddressRequest request = new AddressRequest("Rua A", "100", null, "Centro", "Sao Paulo", "SP", "01000-000", "Brasil");
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);

        AddressResponse response = addressUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.street()).isEqualTo("Rua A");
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void shouldFindById() {
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));

        AddressResponse response = addressUseCase.findById(sampleAddress.getId());

        assertThat(response.street()).isEqualTo("Rua A");
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(addressRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleAddress), 0, 20, 1, 1));

        PageResponse<AddressResponse> responses = addressUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        AddressRequest request = new AddressRequest("Rua B", "200", "Apto 1", "Jardins", "Rio de Janeiro", "RJ", "20000-000", "Brasil");
        Address updated = Address.restore(
                sampleAddress.getId(), "Rua B", "200", "Apto 1", "Jardins",
                "Rio de Janeiro", "RJ", "20000-000", "Brasil",
                sampleAddress.getCreatedAt(), LocalDateTime.now()
        );
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(updated);

        AddressResponse response = addressUseCase.update(sampleAddress.getId(), request);

        assertThat(response.street()).isEqualTo("Rua B");
        assertThat(response.city()).isEqualTo("Rio de Janeiro");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        AddressRequest request = new AddressRequest("Rua B", "200", null, "Jardins", "Rio de Janeiro", "RJ", "20000-000", "Brasil");
        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressUseCase.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        when(addressRepository.existsById(sampleAddress.getId())).thenReturn(true);
        doNothing().when(addressRepository).deleteById(sampleAddress.getId());

        addressUseCase.delete(sampleAddress.getId());

        verify(addressRepository).deleteById(sampleAddress.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(addressRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> addressUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
