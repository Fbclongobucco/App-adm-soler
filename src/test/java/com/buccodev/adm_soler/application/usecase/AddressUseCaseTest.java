package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.address.AddressRequestDto;
import com.buccodev.adm_soler.application.dto.address.AddressResponseDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.repository.AddressRepository;
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
class AddressUseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    private AddressUseCase addressUseCase;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        addressUseCase = new AddressUseCase(addressRepository);
        sampleAddress = Address.create("Rua das Obras", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "BR");
    }

    private static AddressRequestDto request(String street) {
        return new AddressRequestDto(street, "100", null, "Centro", "Sao Paulo", "SP",
                "01000-000", "BR");
    }

    @Test
    void createAddressSavesAndReturnsIt() {
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response = addressUseCase.createAddress(request("Rua das Obras"));

        assertEquals("Rua das Obras", response.street());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void getAddressByIdReturnsIt() {
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));

        assertEquals(sampleAddress.getId(), addressUseCase.getAddressById(sampleAddress.getId()).id());
    }

    @Test
    void getAddressByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressUseCase.getAddressById(id));
    }

    @Test
    void listAddressesMapsThePage() {
        when(addressRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleAddress), 0, 20, 1, 1));

        var page = addressUseCase.listAddresses(0, 20);

        assertEquals(1, page.content().size());
        assertEquals(1, page.totalElements());
    }

    @Test
    void updateAddressAppliesTheChange() {
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        AddressResponseDto response = addressUseCase.updateAddress(sampleAddress.getId(),
                request("Avenida Nova"));

        assertEquals("Avenida Nova", response.street());
    }

    @Test
    void updateAddressThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class,
                () -> addressUseCase.updateAddress(id, request("Rua")));
        verify(addressRepository, never()).save(any());
    }

    @Test
    void deleteAddressRemovesTheEntity() {
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));

        addressUseCase.deleteAddress(sampleAddress.getId());

        verify(addressRepository).delete(sampleAddress);
    }

    @Test
    void deleteAddressThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressUseCase.deleteAddress(id));
        verify(addressRepository, never()).delete(any());
    }
}
