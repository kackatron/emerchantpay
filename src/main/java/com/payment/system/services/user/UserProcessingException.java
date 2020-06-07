package com.payment.system.services.user;

public class UserProcessingException extends Exception {
    public UserProcessingException(String message) {
        super(message);
    }

    public UserProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
