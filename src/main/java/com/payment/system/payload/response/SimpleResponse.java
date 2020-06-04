package com.payment.system.payload.response;


/**
 * SimpleResponse is a simple POJO object containing just one message that have to be returned as a response
 */
public class SimpleResponse {
    private String message;

    public SimpleResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }
}
