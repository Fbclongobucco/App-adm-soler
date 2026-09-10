package com.buccodev.adm_soler.core.domain;

import com.buccodev.adm_soler.core.exception.InvalidRestaurantException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Restaurant {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,11}$");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$");

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private String cnpj;
    private UUID projectId;
    private UUID addressId;
    private Boolean isBilled;
    private BigDecimal lunchPrice;
    private BigDecimal dinnerPrice;
    private BigDecimal additionalValues;
    private Integer days;
    private BigDecimal total;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Restaurant(UUID id, String name, String email, String phone, String cnpj,
                       UUID projectId, UUID addressId, Boolean isBilled, BigDecimal lunchPrice,
                       BigDecimal dinnerPrice, BigDecimal additionalValues, Integer days,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        validate(name, email, phone, cnpj, lunchPrice, dinnerPrice, additionalValues, days);
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.total = calculateTotal();
    }

    public static Restaurant create(String name, String email, String phone, String cnpj,
                                    UUID projectId, UUID addressId, Boolean isBilled,
                                    BigDecimal lunchPrice, BigDecimal dinnerPrice,
                                    BigDecimal additionalValues, Integer days) {
        LocalDateTime now = LocalDateTime.now();
        return new Restaurant(UUID.randomUUID(), name, email, phone, cnpj, projectId, addressId,
                isBilled, lunchPrice, dinnerPrice, additionalValues, days, now, now);
    }

    public static Restaurant restore(UUID id, String name, String email, String phone, String cnpj,
                                     UUID projectId, UUID addressId, Boolean isBilled,
                                     BigDecimal lunchPrice, BigDecimal dinnerPrice,
                                     BigDecimal additionalValues, Integer days,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Restaurant(id, name, email, phone, cnpj, projectId, addressId, isBilled,
                lunchPrice, dinnerPrice, additionalValues, days, createdAt, updatedAt);
    }

    public void update(String name, String email, String phone, String cnpj, UUID projectId,
                       UUID addressId, Boolean isBilled, BigDecimal lunchPrice,
                       BigDecimal dinnerPrice, BigDecimal additionalValues, Integer days) {
        validate(name, email, phone, cnpj, lunchPrice, dinnerPrice, additionalValues, days);
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
        this.total = calculateTotal();
        this.updatedAt = LocalDateTime.now();
    }


    public BigDecimal valuePerEmployee(int employeeCount) {
        if (employeeCount < 0) {
            throw InvalidRestaurantException.negativeEmployeeCount(employeeCount);
        }
        if (employeeCount == 0) {
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(employeeCount), 2, RoundingMode.HALF_UP);
    }

    public boolean isBilled() {
        return Boolean.TRUE.equals(isBilled);
    }

    private BigDecimal calculateTotal() {
        BigDecimal computed = BigDecimal.ZERO;
        if (lunchPrice != null && days != null) {
            computed = computed.add(lunchPrice.multiply(BigDecimal.valueOf(days)));
        }
        if (dinnerPrice != null && days != null) {
            computed = computed.add(dinnerPrice.multiply(BigDecimal.valueOf(days)));
        }
        if (additionalValues != null) {
            computed = computed.add(additionalValues);
        }
        return computed;
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

    private static void validate(String name, String email, String phone, String cnpj,
                                 BigDecimal lunchPrice, BigDecimal dinnerPrice,
                                 BigDecimal additionalValues, Integer days) {
        if (name == null || name.isBlank()) {
            throw InvalidRestaurantException.blankName();
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw InvalidRestaurantException.invalidEmail(email);
        }
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw InvalidRestaurantException.invalidPhone(phone);
        }
        if (cnpj != null && !CNPJ_PATTERN.matcher(cnpj).matches()) {
            throw InvalidRestaurantException.invalidCnpj(cnpj);
        }
        requireNonNegative(lunchPrice, "lunch price");
        requireNonNegative(dinnerPrice, "dinner price");
        requireNonNegative(additionalValues, "additional values");
        if (days != null && days <= 0) {
            throw InvalidRestaurantException.nonPositiveDays(days);
        }
    }

    private static void requireNonNegative(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
            throw InvalidRestaurantException.negativePrice(fieldName, value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
