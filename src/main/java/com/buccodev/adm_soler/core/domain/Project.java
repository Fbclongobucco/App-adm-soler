package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Project {

    private UUID id;
    private String os;
    private String serviceProvided;
    private Client client;
    private List<Restaurant> restaurant;
    private List<Accommodation> accommodation;
    private List<Employee> employee;
    private List<Equipment> equipment;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
