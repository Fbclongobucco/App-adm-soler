package com.buccodev.adm_soler.infra.rest.controllers;

import com.buccodev.adm_soler.application.dto.PageResponseDto;
import com.buccodev.adm_soler.application.dto.employee.EmployeeRequestDto;
import com.buccodev.adm_soler.application.dto.employee.EmployeeResponseDto;
import com.buccodev.adm_soler.application.usecase.EmployeeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Employees", description = "Funcionarios alocados nas obras")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeUseCase employeeUseCase;

    public EmployeeController(EmployeeUseCase employeeUseCase) {
        this.employeeUseCase = employeeUseCase;
    }

    @Operation(summary = "Cria um funcionario", description = "Requer o papel ADMIN.")
    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto request) {
        EmployeeResponseDto created = employeeUseCase.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Busca um funcionario por id")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeUseCase.getEmployeeById(id));
    }

    @Operation(summary = "Lista funcionarios paginados")
    @GetMapping
    public ResponseEntity<PageResponseDto<EmployeeResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(employeeUseCase.listEmployees(page, size));
    }

    @Operation(summary = "Atualiza um funcionario", description = "Requer o papel ADMIN.")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> update(@PathVariable UUID id,
                                                 @Valid @RequestBody EmployeeRequestDto request) {
        return ResponseEntity.ok(employeeUseCase.updateEmployee(id, request));
    }

    @Operation(summary = "Remove um funcionario", description = "Requer o papel ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employeeUseCase.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
