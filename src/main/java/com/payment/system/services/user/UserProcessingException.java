package com.payment.system.services.user;

/**
 * Transaction thrown in case of problem when loading or managing Users.
 */
public class UserProcessingException extends Exception {
    public UserProcessingException(String message) {
        super(message);
    }

    public UserProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
