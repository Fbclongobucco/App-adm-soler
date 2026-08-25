package com.buccodev.adm_soler.application.dto.user;

import com.buccodev.adm_soler.core.domain.User;

public record ChangeRoleRequest(
        User.Role role
) {
}
