package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.Employee;
import com.buccodev.adm_soler.infra.rest.entities.EmployeeJpa;

public class EmployeeMapper {

    public static EmployeeJpa toJpa(Employee domain) {
        if (domain == null) return null;
        EmployeeJpa jpa = new EmployeeJpa();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setEmail(domain.getEmail());
        jpa.setPhone(domain.getPhone());
        jpa.setAddress(AddressMapper.toJpa(domain.getAddress()));
        jpa.setRole(domain.getRole());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static Employee toDomain(EmployeeJpa jpa) {
        if (jpa == null) return null;
        return Employee.restore(
                jpa.getId(),
                jpa.getName(),
                jpa.getEmail(),
                jpa.getPhone(),
                AddressMapper.toDomain(jpa.getAddress()),
                jpa.getRole(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
