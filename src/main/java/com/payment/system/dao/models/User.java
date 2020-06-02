package com.payment.system.dao.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
/**
 * User is a DAO object representing an User in the system.
 *
 * */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"), @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Size(max=255)
    private String description;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private EUserStatus status;

    @Column
    private Long totalTransactionSum;

    @NotBlank
    @Size(max = 120)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EUserStatus getStatus() {
        return status;
    }

    public void setStatus(EUserStatus status) { this.status = status; }

    public Long getTotalTransactionSum() {
        return totalTransactionSum;
    }

    public void setTotalTransactionSum(Long totalTransactionSum) {
        this.totalTransactionSum = totalTransactionSum;
    }
}

