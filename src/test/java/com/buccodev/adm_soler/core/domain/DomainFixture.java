package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;

final class DomainFixture {

    static final LocalDateTime JAN = LocalDateTime.of(2026, 1, 1, 8, 0);
    static final LocalDateTime FEV = LocalDateTime.of(2026, 2, 1, 18, 0);
    static final LocalDateTime MAR = LocalDateTime.of(2026, 3, 1, 8, 0);
    static final LocalDateTime ABR = LocalDateTime.of(2026, 4, 1, 18, 0);

    private DomainFixture() {
    }

    static Address address() {
        return Address.create("Rua das Flores", "123", null, "Centro",
                "Sao Paulo", "SP", "01234-567", "Brasil");
    }

    static Client client() {
        return Client.create("Empresa ABC", "abc@email.com", "11999990000",
                "12.345.678/0001-99", address());
    }

    static Project project() {
        return project(JAN, FEV);
    }

    static Project project(LocalDateTime startDate, LocalDateTime endDate) {
        return Project.create("OS-001", "Manutencao", client(), startDate, endDate);
    }

    static Accommodation accommodation() {
        return Accommodation.create(address(), project(), 4, JAN, FEV);
    }
}
