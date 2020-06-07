package com.payment.system.payload.request;

/**
 * POJO Representing request for deleting user.
 */
public class DeleteUserRequest {
    private String name;

    public DeleteUserRequest() {
    }

    public DeleteUserRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
