package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Employee {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Address address;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
