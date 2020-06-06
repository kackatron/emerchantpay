package com.payment.system.services.trx;

import com.payment.system.controllers.LoadTransactionController;
import com.payment.system.dao.models.trx.*;
import com.payment.system.dao.repositories.TransactionRepository;
import com.payment.system.payload.request.CustomerInfo;
import com.payment.system.payload.request.RegisterTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRegistrationService.class);

    @Autowired
    TransactionRepository transactionRepository;

    public AuthorizeTransaction registerAuthorizationTransaction(RegisterTransaction request) throws TransactionProcessingException {
        // Handle uuid
        String sUuid = request.getUuid();
        long uuid;
        try {
            uuid = Long.parseLong(sUuid);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction UUID", e);
        }
        if (uuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction UUID. Can`t be a negative number or zero");
        }
        // Extract customer information
        CustomerInfo customerInfo = request.getCustomerInfo();
        if (customerInfo == null) {
            throw new TransactionProcessingException("Customer info is required for Authorization transaction.");
        }
        // Handle amount of transaction
        String sAmount = request.getAmount();
        Long amount;
        try {
            amount = Long.parseLong(sAmount);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction amount", e);
        }
        if (amount <= 0) {
            throw new TransactionProcessingException("Invalid amount. Can`t be a negative number or zero");
        }
        // Store transaction
        return transactionRepository.save(new AuthorizeTransaction(uuid, customerInfo.getCustomer_email(), customerInfo.getCustomer_phone(), amount));
    }

    public ReversalTransaction registerReversalTransaction(RegisterTransaction request) throws TransactionProcessingException {
        //Handle uuid
        String sUuid = request.getUuid();
        long uuid;
        try {
            uuid = Long.parseLong(sUuid);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction UUID", e);
        }
        if (uuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction UUID. Can`t be a negative number or zero");
        }
        //Handle reference transaction, we expect it to be AuthorizationTransaction
        String sRefTrx = request.getRefTrx();
        long refUuid;
        try {
            refUuid = Long.parseLong(sRefTrx);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction Referenced Transaction UUID", e);
        }
        if (refUuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction Reference Transaction UUID. Can`t be a negative number or zero");
        }
        Transaction referencedTransaction = transactionRepository.findByUuid(refUuid).orElseThrow(
                () -> new TransactionProcessingException("Can not find the referenced transaction.")
        );
        AuthorizeTransaction authorizeTransaction;
        try {
            authorizeTransaction = (AuthorizeTransaction) referencedTransaction;
        } catch (ClassCastException e) {
            throw new TransactionProcessingException("Referenced transaction is not Authorize transaction.");
        }
        // Store transaction
        authorizeTransaction.setStatus(ETrxStatus.REVERSED);
        transactionRepository.save(authorizeTransaction);
        return transactionRepository.save(new ReversalTransaction(uuid,authorizeTransaction));
    }

    public ChargeTransaction registerChargeTransaction(RegisterTransaction request) throws TransactionProcessingException {
        // Handle uuid
        String sUuid = request.getUuid();
        long uuid;
        try {
            uuid = Long.parseLong(sUuid);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction UUID", e);
        }
        if (uuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction UUID. Can`t be a negative number or zero");
        }
        // Handle reference transaction, we expect it to be AuthorizationTransaction
        String sRefTrx = request.getRefTrx();
        long refUuid;
        try {
            refUuid = Long.parseLong(sRefTrx);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction Referenced Transaction UUID", e);
        }
        if (refUuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction Reference Transaction UUID. Can`t be a negative number or zero");
        }
        Transaction referencedTransaction = transactionRepository.findByUuid(refUuid).orElseThrow(
                () -> new TransactionProcessingException("Can not find the referenced transaction.")
        );
        AuthorizeTransaction authorizeTransaction;
        try {
            authorizeTransaction = (AuthorizeTransaction) referencedTransaction;
        } catch (ClassCastException e) {
            throw new TransactionProcessingException("Referenced transaction is not Authorize transaction.");
        }
        // Handle reference transaction, we expect it to be ChargeTransaction
        String sAmount = request.getAmount();
        long amount;
        try {
            amount = Long.parseLong(sAmount);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction amount", e);
        }
        if (amount <= 0) {
            throw new TransactionProcessingException("Invalid amount. Can`t be a negative number or zero");
        }
        // Store transaction
        return transactionRepository.save(new ChargeTransaction(uuid, authorizeTransaction, amount));
    }

    public RefundTransaction registerRefundTransaction(RegisterTransaction request) throws TransactionProcessingException {
        // Handle uuid
        String sUuid = request.getUuid();
        long uuid;
        try {
            uuid = Long.parseLong(sUuid);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction UUID", e);
        }
        if (uuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction UUID. Can`t be a negative number or zero");
        }
        // Handle reference
        String sRefTrx = request.getRefTrx();
        long refUuid;
        try {
            refUuid = Long.parseLong(sRefTrx);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction Referenced Transaction UUID", e);
        }
        if (refUuid <= 0) {
            throw new TransactionProcessingException("Invalid transaction Reference Transaction UUID. Can`t be a negative number or zero");
        }
        Transaction referencedTransaction = transactionRepository.findByUuid(refUuid).orElseThrow(
                () -> new TransactionProcessingException("Can not find the referenced transaction.")
        );
        ChargeTransaction chargeTransaction;
        try {
            chargeTransaction = (ChargeTransaction) referencedTransaction;
        } catch (ClassCastException e) {
            throw new TransactionProcessingException("Referenced transaction is not Charge transaction.");
        }
        // Handle amount
        String sAmount = request.getAmount();
        long amount;
        try {
            amount = Long.parseLong(sAmount);
        } catch (NumberFormatException e) {
            throw new TransactionProcessingException("Invalid format for transaction amount", e);
        }
        if (amount <= 0) {
            throw new TransactionProcessingException("Invalid amount. Can`t be a negative number or zero");
        }
        if (amount != chargeTransaction.getAmount()) {
            logger.warn("Amount of the referenced Charge Transaction : {} is different from amount in the Refund Transaction :{}",
                    chargeTransaction.getAmount(), amount );
        }
        // Change the status of charge transaction to refunded
        chargeTransaction.setStatus(ETrxStatus.REFUNDED);
        transactionRepository.save(chargeTransaction);

        // Store the refund transaction
        return transactionRepository.save(new RefundTransaction(uuid,chargeTransaction,amount));
    }
}