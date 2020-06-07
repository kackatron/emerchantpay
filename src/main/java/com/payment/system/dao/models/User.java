package com.payment.system.dao.models;

import javax.persistence.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * User is a DAO object representing an User in the system.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"), @UniqueConstraint(columnNames = "email")
        })
public class User {
    //Rename this when you have the time.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private EUserStatus status;

    @Column
    private double totalTransactionSum;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String role;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.totalTransactionSum = 0.0;
    }

    public Long getId() { return id; }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = "ROLE_"+role.toUpperCase();
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

    public void setStatus(EUserStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        switch (status.toLowerCase()) {
            case "active":
                this.status = EUserStatus.ACTIVE;
                break;
            case "inactive":
                this.status = EUserStatus.INACTIVE;
                break;
            default:
                this.status = EUserStatus.INACTIVE;
                break;
        }
    }

    public double getTotalTransactionSum() {
        return totalTransactionSum;
    }

    public void setTotalTransactionSum(double totalTransactionSum) {
        this.totalTransactionSum = totalTransactionSum;
    }


}

