package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Client {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String cnpj;
    private Address address;
    private List<Project> projects;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
