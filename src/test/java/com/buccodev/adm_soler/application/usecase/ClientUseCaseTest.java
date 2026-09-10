package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.client.ClientRequestDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.ClientNotFoundException;
import com.buccodev.adm_soler.core.domain.Client;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.ClientRepository;
import com.buccodev.adm_soler.core.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AddressRepository addressRepository;

    private ClientUseCase clientUseCase;
    private UUID addressId;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        clientUseCase = new ClientUseCase(clientRepository, addressRepository);
        addressId = UUID.randomUUID();
        sampleClient = Client.create("Construtora XYZ", "xyz@xyz.com", "11987654321",
                "11.222.333/0001-44", addressId);
    }

    private ClientRequestDto request(String name) {
        return new ClientRequestDto(name, "xyz@xyz.com", "11987654321", "11.222.333/0001-44", addressId);
    }

    @Test
    void createClientSavesAndReturnsIt() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        var response = clientUseCase.createClient(request("Construtora XYZ"));

        assertEquals("Construtora XYZ", response.name());
        assertEquals(addressId, response.addressId());
    }

    @Test
    void createClientThrowsWhenAddressIsMissing() {
        when(addressRepository.existsById(addressId)).thenReturn(false);

        assertThrows(AddressNotFoundException.class,
                () -> clientUseCase.createClient(request("Construtora XYZ")));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void getClientByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientUseCase.getClientById(id));
    }

    @Test
    void listClientsMapsThePage() {
        when(clientRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleClient), 0, 20, 1, 1));

        assertEquals(1, clientUseCase.listClients(0, 20).content().size());
    }

    @Test
    void updateClientAppliesTheChange() {
        when(clientRepository.findById(sampleClient.getId())).thenReturn(Optional.of(sampleClient));
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Construtora ABC", clientUseCase.updateClient(sampleClient.getId(),
                request("Construtora ABC")).name());
    }

    @Test
    void deleteClientRemovesTheEntity() {
        when(clientRepository.findById(sampleClient.getId())).thenReturn(Optional.of(sampleClient));

        clientUseCase.deleteClient(sampleClient.getId());

        verify(clientRepository).delete(sampleClient);
    }
}
