package com.buccodev.adm_soler.unit.usecase;

import com.buccodev.adm_soler.application.dto.PageResponse;
import com.buccodev.adm_soler.application.dto.employee.EmployeeRequest;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponse;
import com.buccodev.adm_soler.application.exception.ResourceNotFoundException;
import com.buccodev.adm_soler.application.usecase.EmployeeUseCase;
import com.buccodev.adm_soler.core.domain.Address;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
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
class EmployeeUseCaseTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private EmployeeUseCase employeeUseCase;

    private Address sampleAddress;
    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        sampleAddress = Address.restore(
                UUID.randomUUID(), "Rua A", "100", null, "Centro",
                "Sao Paulo", "SP", "01000-000", "Brasil",
                LocalDateTime.now(), LocalDateTime.now()
        );
        sampleEmployee = Employee.restore(
                UUID.randomUUID(), "Funcionario Teste", "func@email.com", "1234567890",
                sampleAddress, "Motorista", LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateEmployee() {
        EmployeeRequest request = new EmployeeRequest("Funcionario Teste", "func@email.com", "1234567890",
                sampleAddress.getId(), "Motorista");
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeUseCase.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Funcionario Teste");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldThrowWhenCreatingWithNonExistentAddress() {
        UUID addressId = UUID.randomUUID();
        EmployeeRequest request = new EmployeeRequest("Funcionario Teste", "func@email.com", "1234567890",
                addressId, "Motorista");
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeUseCase.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindById() {
        when(employeeRepository.findById(sampleEmployee.getId())).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse response = employeeUseCase.findById(sampleEmployee.getId());

        assertThat(response.name()).isEqualTo("Funcionario Teste");
    }

    @Test
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeUseCase.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        when(employeeRepository.findAll(any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(sampleEmployee), 0, 20, 1, 1));

        PageResponse<EmployeeResponse> responses = employeeUseCase.findAll(0, 20);

        assertThat(responses.content()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        EmployeeRequest request = new EmployeeRequest("Funcionario Atualizado", "func@email.com", "1234567890",
                sampleAddress.getId(), "Cozinheiro");
        Employee updated = Employee.restore(
                sampleEmployee.getId(), "Funcionario Atualizado", "func@email.com", "1234567890",
                sampleAddress, "Cozinheiro", sampleEmployee.getCreatedAt(), LocalDateTime.now()
        );
        when(employeeRepository.findById(sampleEmployee.getId())).thenReturn(Optional.of(sampleEmployee));
        when(addressRepository.findById(sampleAddress.getId())).thenReturn(Optional.of(sampleAddress));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updated);

        EmployeeResponse response = employeeUseCase.update(sampleEmployee.getId(), request);

        assertThat(response.name()).isEqualTo("Funcionario Atualizado");
        assertThat(response.role()).isEqualTo("Cozinheiro");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        UUID id = UUID.randomUUID();
        EmployeeRequest request = new EmployeeRequest("Funcionario", "func@email.com", "1234567890",
                sampleAddress.getId(), "Motorista");
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeUseCase.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        when(employeeRepository.existsById(sampleEmployee.getId())).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(sampleEmployee.getId());

        employeeUseCase.delete(sampleEmployee.getId());

        verify(employeeRepository).deleteById(sampleEmployee.getId());
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> employeeUseCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
