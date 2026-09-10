package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurants_tb")
public class RestaurantEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private String cnpj;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    private Boolean isBilled;

    @Column(precision = 19, scale = 2)
    private BigDecimal lunchPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal dinnerPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal additionalValues;

    private Integer days;

    @Column(precision = 19, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected RestaurantEntity() {
    }

    public RestaurantEntity(UUID id, String name, String email, String phone, String cnpj, UUID projectId, UUID addressId, Boolean isBilled, BigDecimal lunchPrice, BigDecimal dinnerPrice, BigDecimal additionalValues, Integer days, BigDecimal total, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cnpj = cnpj;
        this.projectId = projectId;
        this.addressId = addressId;
        this.isBilled = isBilled;
        this.lunchPrice = lunchPrice;
        this.dinnerPrice = dinnerPrice;
        this.additionalValues = additionalValues;
        this.days = days;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCnpj() {
        return cnpj;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public Boolean getIsBilled() {
        return isBilled;
    }

    public BigDecimal getLunchPrice() {
        return lunchPrice;
    }

    public BigDecimal getDinnerPrice() {
        return dinnerPrice;
    }

    public BigDecimal getAdditionalValues() {
        return additionalValues;
    }

    public Integer getDays() {
        return days;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantEntity that = (RestaurantEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
