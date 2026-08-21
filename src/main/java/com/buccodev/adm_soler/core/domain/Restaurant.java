package com.buccodev.adm_soler.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Restaurant {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String cnpj;
    private Project project;
    private List<Employee> employee;
    private Boolean isBilled;
    private BigDecimal lunchPrice;
    private BigDecimal dinnerPrice;
    private BigDecimal total;
    private BigDecimal additionalValues;
    private BigDecimal valuePerEmployee;
    private Integer days;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
