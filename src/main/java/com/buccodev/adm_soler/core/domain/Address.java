package com.buccodev.adm_soler.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Address {

    private UUID id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
