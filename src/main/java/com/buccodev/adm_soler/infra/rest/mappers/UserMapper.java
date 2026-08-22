package com.buccodev.adm_soler.infra.rest.mappers;

import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.infra.rest.entities.UserJpa;

public class UserMapper {

    public static UserJpa toJpa(User domain) {
        if (domain == null) return null;
        UserJpa jpa = new UserJpa();
        jpa.setId(domain.getId());
        jpa.setName(domain.getName());
        jpa.setEmail(domain.getEmail());
        jpa.setPassword(domain.getPassword());
        jpa.setPhone(domain.getPhone());
        jpa.setRole(domain.getRole() != null ? domain.getRole().name() : null);
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    public static User toDomain(UserJpa jpa) {
        if (jpa == null) return null;
        return User.restore(
                jpa.getId(),
                jpa.getName(),
                jpa.getEmail(),
                jpa.getPassword(),
                jpa.getPhone(),
                jpa.getRole() != null ? User.Role.valueOf(jpa.getRole()) : null,
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}
