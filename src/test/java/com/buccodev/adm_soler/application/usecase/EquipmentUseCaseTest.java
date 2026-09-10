package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequestDto;
import com.buccodev.adm_soler.application.exception.EquipmentNotFoundException;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
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
class EquipmentUseCaseTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    private EquipmentUseCase equipmentUseCase;
    private Equipment sampleEquipment;

    @BeforeEach
    void setUp() {
        equipmentUseCase = new EquipmentUseCase(equipmentRepository);
        sampleEquipment = Equipment.create("Andaime", "Andaime tubular");
    }

    @Test
    void createEquipmentSavesAndReturnsIt() {
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(i -> i.getArgument(0));

        var response = equipmentUseCase.createEquipment(new EquipmentRequestDto("Andaime", "tubular"));

        assertEquals("Andaime", response.name());
    }

    @Test
    void getEquipmentByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(equipmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EquipmentNotFoundException.class, () -> equipmentUseCase.getEquipmentById(id));
    }

    @Test
    void listEquipmentsMapsThePage() {
        when(equipmentRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleEquipment), 0, 20, 1, 1));

        assertEquals(1, equipmentUseCase.listEquipments(0, 20).content().size());
    }

    @Test
    void updateEquipmentAppliesTheChange() {
        when(equipmentRepository.findById(sampleEquipment.getId()))
                .thenReturn(Optional.of(sampleEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(i -> i.getArgument(0));

        var response = equipmentUseCase.updateEquipment(sampleEquipment.getId(),
                new EquipmentRequestDto("Linha de vida", "Cabo 8mm"));

        assertEquals("Linha de vida", response.name());
    }

    @Test
    void deleteEquipmentRemovesTheEntity() {
        when(equipmentRepository.findById(sampleEquipment.getId()))
                .thenReturn(Optional.of(sampleEquipment));

        equipmentUseCase.deleteEquipment(sampleEquipment.getId());

        verify(equipmentRepository).delete(sampleEquipment);
    }
}
