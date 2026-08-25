package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class RestaurantJpa implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private String cnpj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectJpa project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "restaurant_employees",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<EmployeeJpa> employees = new HashSet<>();

    private Boolean isBilled;

    @Column(precision = 10, scale = 2)
    private BigDecimal lunchPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal dinnerPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(precision = 10, scale = 2)
    private BigDecimal additionalValues;

    @Column(precision = 10, scale = 2)
    private BigDecimal valuePerEmployee;

    private Integer days;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private AddressJpa address;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity = true;

    public RestaurantJpa() {
    }

    public RestaurantJpa(UUID id, String name, String email, String phone, String cnpj,
                         ProjectJpa project, Boolean isBilled, BigDecimal lunchPrice,
                         BigDecimal dinnerPrice, BigDecimal total, BigDecimal additionalValues,
                         BigDecimal valuePerEmployee, Integer days, AddressJpa address,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cnpj = cnpj;
        this.project = project;
        this.isBilled = isBilled;
        this.lunchPrice = lunchPrice;
        this.dinnerPrice = dinnerPrice;
        this.total = total;
        this.additionalValues = additionalValues;
        this.valuePerEmployee = valuePerEmployee;
        this.days = days;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Toda entidade que veio do banco existe, por definicao.
     *
     * Sem este callback o campo transiente {@code newEntity} continuava {@code true}
     * numa entidade recem-carregada, e o {@code delete} do Spring Data desiste em
     * silencio quando {@link #isNew()} responde {@code true}: o DELETE devolvia 204
     * sem apagar nada. Os adapters chamam {@code markAsExisting()} apos os seus
     * proprios findById, mas o {@code deleteById} faz uma busca interna que nao
     * passa por eles.
     */
    @PostLoad
    void markLoadedAsExisting() {
        this.newEntity = false;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }

    public void markAsExisting() {
        this.newEntity = false;
    }

    @Override
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public ProjectJpa getProject() { return project; }
    public void setProject(ProjectJpa project) { this.project = project; }

    public Set<EmployeeJpa> getEmployees() { return employees; }
    public void setEmployees(Set<EmployeeJpa> employees) { this.employees = employees; }

    public Boolean getIsBilled() { return isBilled; }
    public void setIsBilled(Boolean isBilled) { this.isBilled = isBilled; }

    public BigDecimal getLunchPrice() { return lunchPrice; }
    public void setLunchPrice(BigDecimal lunchPrice) { this.lunchPrice = lunchPrice; }

    public BigDecimal getDinnerPrice() { return dinnerPrice; }
    public void setDinnerPrice(BigDecimal dinnerPrice) { this.dinnerPrice = dinnerPrice; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getAdditionalValues() { return additionalValues; }
    public void setAdditionalValues(BigDecimal additionalValues) { this.additionalValues = additionalValues; }

    public BigDecimal getValuePerEmployee() { return valuePerEmployee; }
    public void setValuePerEmployee(BigDecimal valuePerEmployee) { this.valuePerEmployee = valuePerEmployee; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }

    public AddressJpa getAddress() { return address; }
    public void setAddress(AddressJpa address) { this.address = address; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
