package com.payment.system.services.trx;

public class TransactionProcessingException extends Exception{
    public TransactionProcessingException(String message){
        super(message);
    }
    public TransactionProcessingException(String message, Throwable cause){
        super(message,cause);
    }
}
