package com.buccodev.adm_soler.application.dto.client;

import java.util.UUID;

public record ClientRequest(
        String name,
        String email,
        String phone,
        String cnpj,
        UUID addressId
) {
}
