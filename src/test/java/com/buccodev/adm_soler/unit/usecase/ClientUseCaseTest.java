package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.client.ClientRequest;
import com.buccodev.adm_soler.application.dto.client.ClientResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.ClientUseCase;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.PageQuery;
import com.buccodev.adm_soler.core.repository.PageResult;
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
class ClientUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private ClientUseCase clientUseCase;

    private Address sampleAddress;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        sampleAddress = Address.restore(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleClient = Client.restore(
                UUID.randomUUID(), "Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress, Collections.emptySet(),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateClient() {
        ClientRequest request = new ClientRequest("Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress.getId());
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(clientRepository.save(any(Client.class))).thenReturn(sampleClient);

        ClientResponse response = clientUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Cliente Teste");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentAddress() {
        UUID addressId = UUID.randomUUID();
        ClientRequest request = new ClientRequest("Cliente Teste", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", addressId);
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindById() {
        when(clientRepository.findById(sampleClient.getId())).thenReturn(Optional.of(sampleClient));

        ClientResponse response = clientUseCase.findById(sampleClient.getId());

        assertThat(response.name()).isEqualTo("Cliente Teste");
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(clientRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleClient), 0, 20, 1, 1));

        PageResponse<ClientResponse> responses = clientUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        ClientRequest request = new ClientRequest("Cliente Atualizado", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress.getId());
        Client updated = Client.restore(
                sampleClient.getId(), "Cliente Atualizado", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress, Collections.emptySet(),
                sampleClient.getCreatedAt(), LocalDateTime.now()
        );
        when(clientRepository.findById(sampleClient.getId())).thenReturn(Optional.of(sampleClient));
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(clientRepository.save(any(Client.class))).thenReturn(updated);

        ClientResponse response = clientUseCase.update(sampleClient.getId(), request);

        assertThat(response.name()).isEqualTo("Cliente Atualizado");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        ClientRequest request = new ClientRequest("Cliente", "cliente@email.com", "1234567890",
                "12.345.678/0001-99", sampleAddress.getId());
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientUseCase.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        when(clientRepository.existsById(sampleClient.getId())).thenReturn(true);
        doNothing().when(clientRepository).deleteById(sampleClient.getId());

        clientUseCase.delete(sampleClient.getId());

        verify(clientRepository).deleteById(sampleClient.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(clientRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> clientUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
