package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Accommodation {

    private UUID id;
    private Address address;
    private Project project;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Employee> employee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
