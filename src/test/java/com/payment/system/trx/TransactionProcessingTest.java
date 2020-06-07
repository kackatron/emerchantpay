package com.payment.system.trx;


import com.payment.system.controllers.LoadTransactionController;
import com.payment.system.dao.models.trx.*;
import com.payment.system.dao.repositories.trx.TransactionRepository;
import com.payment.system.payload.request.CustomerInfo;
import com.payment.system.payload.request.RegisterTransaction;
import com.payment.system.security.UserDetailsImpl;
import com.payment.system.services.trx.TransactionCleanupService;
import com.payment.system.services.trx.TransactionProcessingException;
import com.payment.system.services.trx.TransactionProcessingService;
import com.payment.system.services.user.UserLoadService;
import com.payment.system.dao.models.User;
import com.payment.system.dao.repositories.user.UserRepository;
import com.payment.system.services.trx.TransactionRegistrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TransactionProcessingTest {

    @Autowired
    TransactionRegistrationService transactionRegistrationService;

    @Autowired
    UserLoadService userLoadService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionProcessingService transactionProcessingService;

    @Autowired
    TransactionCleanupService transactionCleanupService;


    private User testUser;

    private CustomerInfo customerInfo;

    //Tests a loading of Users from given csv.
    @BeforeEach
    public void setup() {
        userLoadService.setCsvFile("classpath:TestUsers.csv");
        testUser = userLoadService.loadUsers().get(0);
        assertNotNull(testUser, "Loading of user failed, it returned Null");
        // This is the only user in TestUsers.csv, on later date make this comparison dynamic.
        assertEquals("Test", testUser.getName());
        assertEquals("test@gmai.com", testUser.getEmail());

        customerInfo = new CustomerInfo("Customer1", "999-999-999");
    }

    @Test
    public void processHappyPath() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, LoadTransactionController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);
        RegisterTransaction registerChargeTransaction =
                new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                        LoadTransactionController.CHARGE, null);
        transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);

        transactionProcessingService.processTransactions();

        testUser = userRepository.findByName(testUser.getName()).orElseThrow(() -> new TransactionProcessingException("Can not find the test user"));
        assertEquals(testUser.getTotalTransactionSum(), authorizeTransaction.getAmount(),
                "The total amount of the merchant is different from the amount in transactions");
    }

    @Test
    public void processRevert() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, LoadTransactionController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

        RegisterTransaction registerChargeTransaction =
                new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                        LoadTransactionController.CHARGE, null);
        ChargeTransaction chargeTransaction = transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);

        RegisterTransaction registerRefundTransaction = new RegisterTransaction("3", "100.",
                chargeTransaction.getUuid().toString(), LoadTransactionController.REFUND, null);
        transactionRegistrationService.registerRefundTransaction(registerRefundTransaction);

        transactionProcessingService.processTransactions();

        testUser = userRepository.findByName(testUser.getName()).orElseThrow(() -> new TransactionProcessingException("Can not find the test user"));
        assertEquals(0.0, testUser.getTotalTransactionSum(),
                "The total amount of the merchant is different from the amount in transactions");
    }

    @Test
    public void ProcessReverse() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, LoadTransactionController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

        RegisterTransaction registerReverseTransaction = new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                LoadTransactionController.REVERSAL, null);
        transactionRegistrationService.registerReversalTransaction(registerReverseTransaction);
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        transactionProcessingService.processTransactions();
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));
        assertEquals(ETrxStatus.REVERSED,authorizeTransaction.getStatus(), "Authorization transaction is not in reversed state after registering a reversal transaction.");
    }

    @Test
    public void ProcessReverseAfterCharge() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, LoadTransactionController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

        RegisterTransaction registerChargeTransaction =
                new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                        LoadTransactionController.CHARGE, null);
        transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);

        RegisterTransaction registerReverseTransaction = new RegisterTransaction("3", "100.0",
                authorizeTransaction.getUuid().toString(), LoadTransactionController.REVERSAL, null);

        ReversalTransaction reversalTransaction = transactionRegistrationService.registerReversalTransaction(registerReverseTransaction);
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        transactionProcessingService.processTransactions();
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        reversalTransaction = (ReversalTransaction) transactionRepository.findByUuid(reversalTransaction
                .getUuid()).orElseThrow(()->new TransactionProcessingException("Can not find the reversal transaction"));

        assertEquals(ETrxStatus.ERROR,reversalTransaction.getStatus(), "Authorization transaction is reversed but it was already charged.");
        assertEquals(ETrxStatus.APPROVED,authorizeTransaction.getStatus(), "Authorization transaction is reversed but it was already charged.");
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        //Set the age limit for time to live to be two seconds in the future, since there is a one second sporadic
        //difference that appears from time to time, and it makes transactions to be out of the searching constraint for
        //deletion.
        transactionCleanupService.setTrxAge(-2000);
        transactionCleanupService.cleanTransactions();
        userRepository.delete(testUser);
    }
}
