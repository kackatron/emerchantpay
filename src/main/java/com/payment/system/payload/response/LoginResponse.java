package com.payment.system.payload.response;

import java.util.List;

/**
 * LoginResponse is POJO that contains the information of a given user needed for its UI.
 */
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String description;
    private String email;
    private String status;
    private double totalTransactionSum;
    private List<String> roles;

    public LoginResponse(String token, Long id, String username, String email, String description, String status, double totalTransactionSum, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.description = description;
        this.status = status;
        this.totalTransactionSum = totalTransactionSum;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalTransactionSum() {
        return totalTransactionSum;
    }

    public void setTotalTransactionSum(Long totalTransactionSum) {
        this.totalTransactionSum = totalTransactionSum;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}