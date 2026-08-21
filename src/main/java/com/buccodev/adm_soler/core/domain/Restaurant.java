package com.buccodev.adm_soler.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Restaurant {

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private String cnpj;
    private Project project;
    private final Set<Employee> employees = new HashSet<>();
    private Boolean isBilled;
    private BigDecimal lunchPrice;
    private BigDecimal dinnerPrice;
    private BigDecimal total;
    private BigDecimal additionalValues;
    private BigDecimal valuePerEmployee;
    private Integer days;
    private Address address;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Restaurant(UUID id, String name, String email, String phone, String cnpj,
                       Project project, Set<Employee> employees, Boolean isBilled, BigDecimal lunchPrice,
                       BigDecimal dinnerPrice, BigDecimal total, BigDecimal additionalValues,
                       BigDecimal valuePerEmployee, Integer days, Address address,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = Objects.requireNonNull(name, "name is required");
        this.email = email;
        this.phone = phone;
        this.cnpj = cnpj;
        this.project = project;
        if (employees != null) {
            this.employees.addAll(employees);
        }
        this.isBilled = isBilled;
        this.lunchPrice = lunchPrice;
        this.dinnerPrice = dinnerPrice;
        this.total = total;
        this.additionalValues = additionalValues;
        this.valuePerEmployee = valuePerEmployee;
        this.days = days;
        this.address = address;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Restaurant create(String name, String email, String phone, Project project,
                                    Boolean isBilled, Integer days, Address address) {
        var now = LocalDateTime.now();
        return new Restaurant(UUID.randomUUID(), name, email, phone, null, project, null,
                isBilled, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, days, address, now, now);
    }

    public static Restaurant from(UUID id, String name, String email, String phone, String cnpj,
                                  Project project, Set<Employee> employees, Boolean isBilled,
                                  BigDecimal lunchPrice, BigDecimal dinnerPrice, BigDecimal total,
                                  BigDecimal additionalValues, BigDecimal valuePerEmployee, Integer days,
                                  Address address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Restaurant(id, name, email, phone, cnpj, project, employees, isBilled, lunchPrice,
                dinnerPrice, total, additionalValues, valuePerEmployee, days, address, createdAt, updatedAt);
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

    public Project getProject() {
        return project;
    }

    public Set<Employee> getEmployees() {
        return Collections.unmodifiableSet(employees);
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

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getAdditionalValues() {
        return additionalValues;
    }

    public BigDecimal getValuePerEmployee() {
        return valuePerEmployee;
    }

    public Integer getDays() {
        return days;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name is required");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setIsBilled(Boolean isBilled) {
        this.isBilled = isBilled;
    }

    public void setLunchPrice(BigDecimal lunchPrice) {
        this.lunchPrice = lunchPrice;
        calculateTotal();
    }

    public void setDinnerPrice(BigDecimal dinnerPrice) {
        this.dinnerPrice = dinnerPrice;
        calculateTotal();
    }

    public void setAdditionalValues(BigDecimal additionalValues) {
        this.additionalValues = additionalValues;
        calculateTotal();
    }

    public void setValuePerEmployee(BigDecimal valuePerEmployee) {
        this.valuePerEmployee = valuePerEmployee;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "employee is required");
        this.employees.add(employee);
    }

    public void addAllEmployees(Collection<Employee> employees) {
        Objects.requireNonNull(employees, "employees is required");
        this.employees.addAll(employees);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
    }

    public void calculateTotal() {
        var total = BigDecimal.ZERO;
        if (lunchPrice != null && days != null) {
            total = total.add(lunchPrice.multiply(BigDecimal.valueOf(days)));
        }
        if (dinnerPrice != null && days != null) {
            total = total.add(dinnerPrice.multiply(BigDecimal.valueOf(days)));
        }
        if (additionalValues != null) {
            total = total.add(additionalValues);
        }
        this.total = total;
    }

    public boolean isBilled() {
        return Boolean.TRUE.equals(isBilled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
