package com.payment.system.services.trx;

/**
 * Exception that is thrown when there is an error in retrieving transactions.
 */
public class TransactionRetrievalException extends Exception {
    public TransactionRetrievalException(String message) {
        super(message);
    }

    public TransactionRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
