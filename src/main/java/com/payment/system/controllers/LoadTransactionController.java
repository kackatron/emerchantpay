package com.payment.system.controllers;

import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.payload.request.RegisterTransaction;
import com.payment.system.security.UserDetailsImpl;
import com.payment.system.services.trx.TransactionProcessingException;
import com.payment.system.services.trx.TransactionRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class LoadTransactionController {
    private static final Logger logger = LoggerFactory.getLogger(LoadTransactionController.class);

    @Autowired
    TransactionRegistrationService transactionRegistrationService;

    @RequestMapping("/load")
    public ResponseEntity loadPayment(@RequestBody RegisterTransaction registerTransaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication;
        logger.info("Received {} from merchant {}", registerTransaction, userDetails);
        Transaction transaction = null;
        switch (registerTransaction.getRefTrx()) {
            case "Authorization": {
                logger.info("Transaction is of type Authorization");
                try {
                    transaction = transactionRegistrationService.registerAuthorizationTransaction(registerTransaction);
                    logger.info("Successful registering of transaction {}", transaction);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(transaction);
                } catch (TransactionProcessingException e) {
                    logger.error("Failed registering Authorization transaction ", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
                }
            }
            case "Charge": {
                logger.info("Transaction is of type Charge");
                try {
                    transaction = transactionRegistrationService.registerChargeTransaction(registerTransaction);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(transaction);
                } catch (TransactionProcessingException e) {
                    logger.error("Failed to register Charge transaction", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
                }
            }
            case "Refund": {
                logger.info("Transaction is of type Refund");
                try {
                    transaction = transactionRegistrationService.registerRefundTransaction(registerTransaction);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(transaction);
                } catch (TransactionProcessingException e) {
                    logger.error("Failed to register Charge transaction", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
                }
            }
            case "Reversal":
            default: {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new TransactionProcessingException("There is no transaction with type " + registerTransaction.getRefTrx()));
            }
        }
    }
}
