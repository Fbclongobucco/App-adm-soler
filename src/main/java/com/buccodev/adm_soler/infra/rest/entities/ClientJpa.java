package com.buccodev.adm_soler.infra.rest.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class ClientJpa implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private String cnpj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressJpa address;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<ProjectJpa> projects = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity = true;

    public ClientJpa() {
    }

    public ClientJpa(UUID id, String name, String email, String phone, String cnpj,
                     AddressJpa address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cnpj = cnpj;
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

    public AddressJpa getAddress() { return address; }
    public void setAddress(AddressJpa address) { this.address = address; }

    public Set<ProjectJpa> getProjects() { return projects; }
    public void setProjects(Set<ProjectJpa> projects) { this.projects = projects; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
