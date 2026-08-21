package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public enum Role {
        ADMIN, USER, FOREIGN;
    }
}
