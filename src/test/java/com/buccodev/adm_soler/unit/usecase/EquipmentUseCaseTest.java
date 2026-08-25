package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.equipment.EquipmentRequest;
import com.buccodev.adm_soler.application.dto.equipment.EquipmentResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.EquipmentUseCase;
import com.buccodev.adm_soler.core.domain.Equipment;
import com.buccodev.adm_soler.core.repository.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentUseCaseTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentUseCase equipmentUseCase;

    private Equipment sampleEquipment;

    @BeforeEach
    void setUp() {
        sampleEquipment = Equipment.restore(
                UUID.randomUUID(),
                "Notebook Dell",
                "Notebook para desenvolvimento",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateEquipment() {
        EquipmentRequest request = new EquipmentRequest("Notebook Dell", "Notebook para dev");
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(sampleEquipment);

        EquipmentResponse response = equipmentUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Notebook Dell");
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    void shouldFindById() {
        when(equipmentRepository.findById(sampleEquipment.getId())).thenReturn(Optional.of(sampleEquipment));

        EquipmentResponse response = equipmentUseCase.findById(sampleEquipment.getId());

        assertThat(response.name()).isEqualTo("Notebook Dell");
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(equipmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(equipmentRepository.findAll()).thenReturn(List.of(sampleEquipment));

        List<EquipmentResponse> responses = equipmentUseCase.findAll();

        assertThat(responses).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        EquipmentRequest request = new EquipmentRequest("Notebook HP", "Atualizado");
        Equipment updated = Equipment.restore(
                sampleEquipment.getId(), "Notebook HP", "Atualizado",
                sampleEquipment.getCreatedAt(), java.time.LocalDateTime.now()
        );
        when(equipmentRepository.findById(sampleEquipment.getId())).thenReturn(Optional.of(sampleEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(updated);

        EquipmentResponse response = equipmentUseCase.update(sampleEquipment.getId(), request);

        assertThat(response.name()).isEqualTo("Notebook HP");
    }

    @Test
    void shouldDelete() {
        when(equipmentRepository.existsById(sampleEquipment.getId())).thenReturn(true);
        doNothing().when(equipmentRepository).deleteById(sampleEquipment.getId());

        equipmentUseCase.delete(sampleEquipment.getId());

        verify(equipmentRepository).deleteById(sampleEquipment.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(equipmentRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> equipmentUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
