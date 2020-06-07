package com.payment.system.services.trx;
/**
 * Exception that is thrown when registering or processing transactions.
 */
public class TransactionProcessingException extends Exception{
    public TransactionProcessingException(String message){
        super(message);
    }
    public TransactionProcessingException(String message, Throwable cause){
        super(message,cause);
    }
}
