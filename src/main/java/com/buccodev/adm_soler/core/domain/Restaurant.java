package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.application.exception.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class Restaurant {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");

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
                      Project project, Boolean isBilled, BigDecimal lunchPrice,
                      BigDecimal dinnerPrice, BigDecimal total, BigDecimal additionalValues,
                      BigDecimal valuePerEmployee, Integer days, Address address,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.phone = validatePhone(phone);
        this.cnpj = validateCnpj(cnpj);
        this.project = project;
        this.isBilled = isBilled;
        this.lunchPrice = validatePrice(lunchPrice, "lunchPrice");
        this.dinnerPrice = validatePrice(dinnerPrice, "dinnerPrice");
        this.total = total;
        this.additionalValues = validatePrice(additionalValues, "additionalValues");
        this.valuePerEmployee = valuePerEmployee;
        this.days = validateDays(days);
        this.address = address;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = updatedAt;
    }

    public static Restaurant create(String name, String email, String phone, String cnpj,
                                    Project project, Boolean isBilled, BigDecimal lunchPrice,
                                    BigDecimal dinnerPrice, BigDecimal additionalValues,
                                    Integer days, Address address) {
        var now = LocalDateTime.now();
        var restaurant = new Restaurant(UUID.randomUUID(), name, email, phone, cnpj, project,
                isBilled, orZero(lunchPrice), orZero(dinnerPrice), BigDecimal.ZERO,
                orZero(additionalValues), BigDecimal.ZERO, days, address, now, now);
        restaurant.calculateTotal();
        restaurant.calculateValuePerEmployee();
        return restaurant;
    }

    private static BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public static Restaurant restore(UUID id, String name, String email, String phone, String cnpj,
                                     Project project, Set<Employee> employees, Boolean isBilled,
                                     BigDecimal lunchPrice, BigDecimal dinnerPrice, BigDecimal total,
                                     BigDecimal additionalValues, BigDecimal valuePerEmployee, Integer days,
                                     Address address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        var restaurant = new Restaurant(id, name, email, phone, cnpj, project, isBilled, lunchPrice,
                dinnerPrice, total, additionalValues, valuePerEmployee, days, address, createdAt, updatedAt);
        if (employees != null) {
            restaurant.employees.addAll(employees);
        }
        restaurant.calculateTotal();
        restaurant.calculateValuePerEmployee();
        return restaurant;
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
        this.name = validateName(name);
    }

    public void setEmail(String email) {
        this.email = validateEmail(email);
    }

    public void setPhone(String phone) {
        this.phone = validatePhone(phone);
    }

    public void setCnpj(String cnpj) {
        this.cnpj = validateCnpj(cnpj);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setIsBilled(Boolean isBilled) {
        this.isBilled = isBilled;
    }

    public void setLunchPrice(BigDecimal lunchPrice) {
        this.lunchPrice = validatePrice(lunchPrice, "lunchPrice");
        calculateTotal();
        calculateValuePerEmployee();
    }

    public void setDinnerPrice(BigDecimal dinnerPrice) {
        this.dinnerPrice = validatePrice(dinnerPrice, "dinnerPrice");
        calculateTotal();
        calculateValuePerEmployee();
    }

    public void setAdditionalValues(BigDecimal additionalValues) {
        this.additionalValues = validatePrice(additionalValues, "additionalValues");
        calculateTotal();
        calculateValuePerEmployee();
    }

    public void setDays(Integer days) {
        this.days = validateDays(days);
        calculateTotal();
        calculateValuePerEmployee();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "employee is required");
        this.employees.add(employee);
        calculateValuePerEmployee();
    }

    public void addAllEmployees(Collection<Employee> employees) {
        Objects.requireNonNull(employees, "employees is required");
        this.employees.addAll(employees);
        calculateValuePerEmployee();
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        calculateValuePerEmployee();
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

    public void calculateValuePerEmployee() {
        if (total != null && !employees.isEmpty()) {
            this.valuePerEmployee = total.divide(BigDecimal.valueOf(employees.size()), 2, java.math.RoundingMode.HALF_UP);
        } else {
            this.valuePerEmployee = BigDecimal.ZERO;
        }
    }

    public boolean isBilled() {
        return Boolean.TRUE.equals(isBilled);
    }

    private String validateName(String name) {
        Objects.requireNonNull(name, "name is required");
        if (name.isBlank()) {
            throw new BadRequestException("name cannot be blank");
        }
        return name;
    }

    private String validateEmail(String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("invalid email format");
        }
        return email;
    }

    private String validatePhone(String phone) {
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BadRequestException("invalid phone format");
        }
        return phone;
    }

    private String validateCnpj(String cnpj) {
        if (cnpj != null && !CNPJ_PATTERN.matcher(cnpj).matches()) {
            throw new BadRequestException("invalid CNPJ format");
        }
        return cnpj;
    }

    private BigDecimal validatePrice(BigDecimal price, String fieldName) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(fieldName + " cannot be negative");
        }
        return price;
    }

    private Integer validateDays(Integer days) {
        if (days != null && days <= 0) {
            throw new BadRequestException("days must be greater than zero");
        }
        return days;
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

}
