package com.payment.system.controllers;

import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.payload.request.RegisterTransaction;
import com.payment.system.security.UserDetailsImpl;
import com.payment.system.services.trx.TransactionProcessingException;
import com.payment.system.services.trx.TransactionRegistrationService;
import com.payment.system.services.trx.TransactionRetrievalException;
import com.payment.system.services.trx.TransactionRetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TransactionManagementController is a specialized controller that is  providing all transaction related REST endpoints.
 */
@RestController
@RequestMapping("/trx")
public class TransactionManagementController {
    //Exposed for test purposes
    public static final String AUTHORIZATION = "Authorization";
    public static final String CHARGE = "Charge";
    public static final String REFUND = "Refund";
    public static final String REVERSAL = "Reversal";

    private static final Logger logger = LoggerFactory.getLogger(TransactionManagementController.class);

    @Autowired
    TransactionRegistrationService transactionRegistrationService;

    @Autowired
    TransactionRetrievalService transactionRetrievalService;

    /**
     * The endpoint used for registering all the types of transactions.
     * @param registerTransaction - POJO representing the registering request.
     * @return HTTP status and registered Transaction UUID
     */
    @RequestMapping("/load")
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public ResponseEntity registerTransaction(@RequestBody RegisterTransaction registerTransaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails.getStatus().equals("INACTIVE")) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Merchant is INACTIVE");
        }
        logger.info("Received {} from merchant {}", registerTransaction, userDetails);
        Transaction transaction = null;
        switch (registerTransaction.getTypeOfTrx()) {
            case AUTHORIZATION: {
                logger.info("Transaction is of type Authorization");
                try {
                    transaction = transactionRegistrationService.registerAuthorizationTransaction(userDetails,registerTransaction);
                    logger.info("Successful registering of transaction {}", transaction);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(String.valueOf(transaction.getUuid()));
                } catch (TransactionProcessingException e) {
                    logger.error("Failed registering Authorization transaction ", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
                }
            }
            case CHARGE: {
                logger.info("Transaction is of type Charge");
                try {
                    transaction = transactionRegistrationService.registerChargeTransaction(registerTransaction);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(String.valueOf(transaction.getUuid()));
                } catch (TransactionProcessingException e) {
                    logger.error("Failed to register Charge transaction", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
                }
            }
            case REFUND: {
                logger.info("Transaction is of type Refund");
                try {
                    transaction = transactionRegistrationService.registerRefundTransaction(registerTransaction);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(String.valueOf(transaction.getUuid()));
                } catch (TransactionProcessingException e) {
                    logger.error("Failed to register Charge transaction", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
                }
            }
            case REVERSAL:
            default: {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new TransactionProcessingException("There is no transaction with type " + registerTransaction.getRefTrx()));
            }
        }
    }

    /**
     * Endpoint that returns all the transactions for the current user.
     * @return all the transactions for the current user
     */
    @RequestMapping("/retrieve")
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    public ResponseEntity retrieveTransaction() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("Retrieve all current transactions for merchant {}",  userDetails);
        try {
            List<Transaction> transactionList = transactionRetrievalService.retrieveTransactionsForUser(userDetails);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(transactionList);
        } catch (TransactionRetrievalException e) {
            logger.error("Failed to retrieve transactions", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }
}
