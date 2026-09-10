package com.buccodev.adm_soler.application.mapper;

import com.buccodev.adm_soler.application.dto.address.AddressRequestDto;
import com.buccodev.adm_soler.core.domain.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {

    @Test
    void toDomainBuildsANewAddress() {
        Address address = AddressMapper.toDomain(new AddressRequestDto("Rua das Obras", "100",
                "Galpao", "Centro", "Sao Paulo", "SP", "01000-000", "BR"));

        assertNotNull(address.getId());
        assertEquals("Rua das Obras", address.getStreet());
        assertEquals("BR", address.getCountry());
    }

    @Test
    void toResponseDtoCopiesEveryField() {
        Address address = Address.create("Rua", "1", "Fundos", "Bairro", "Campinas", "SP",
                "13000-000", "BR");

        var dto = AddressMapper.toResponseDto(address);

        assertEquals(address.getId(), dto.id());
        assertEquals("Rua", dto.street());
        assertEquals("Fundos", dto.complement());
        assertEquals("Campinas", dto.city());
        assertEquals(address.getCreatedAt(), dto.createdAt());
    }
}
