package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.application.dto.employee.EmployeeRequestDto;
import com.buccodev.adm_soler.application.exception.AddressNotFoundException;
import com.buccodev.adm_soler.application.exception.EmployeeNotFoundException;
import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.core.repository.AddressRepository;
import com.buccodev.adm_soler.core.repository.EmployeeRepository;
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
class EmployeeUseCaseTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AddressRepository addressRepository;

    private EmployeeUseCase employeeUseCase;
    private UUID addressId;
    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        employeeUseCase = new EmployeeUseCase(employeeRepository, addressRepository);
        addressId = UUID.randomUUID();
        sampleEmployee = Employee.create("Maria Souza", "maria@soler.com", "11987654321",
                addressId, "PINTOR");
    }

    private EmployeeRequestDto request(String role) {
        return new EmployeeRequestDto("Maria Souza", "maria@soler.com", "11987654321", addressId, role);
    }

    @Test
    void createEmployeeSavesAndReturnsIt() {
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("PINTOR", employeeUseCase.createEmployee(request("PINTOR")).role());
    }

    @Test
    void createEmployeeThrowsWhenAddressIsMissing() {
        when(addressRepository.existsById(addressId)).thenReturn(false);

        assertThrows(AddressNotFoundException.class,
                () -> employeeUseCase.createEmployee(request("PINTOR")));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getEmployeeByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeUseCase.getEmployeeById(id));
    }

    @Test
    void listEmployeesMapsThePage() {
        when(employeeRepository.findAll(any(Repository.PageQuery.class)))
                .thenReturn(new Repository.PageResult<>(List.of(sampleEmployee), 0, 20, 1, 1));

        assertEquals(1, employeeUseCase.listEmployees(0, 20).content().size());
    }

    @Test
    void updateEmployeeAppliesTheChange() {
        when(employeeRepository.findById(sampleEmployee.getId()))
                .thenReturn(Optional.of(sampleEmployee));
        when(addressRepository.existsById(addressId)).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("ALPINISTA INDUSTRIAL", employeeUseCase.updateEmployee(sampleEmployee.getId(),
                request("ALPINISTA INDUSTRIAL")).role());
    }

    @Test
    void deleteEmployeeRemovesTheEntity() {
        when(employeeRepository.findById(sampleEmployee.getId()))
                .thenReturn(Optional.of(sampleEmployee));

        employeeUseCase.deleteEmployee(sampleEmployee.getId());

        verify(employeeRepository).delete(sampleEmployee);
    }
}
