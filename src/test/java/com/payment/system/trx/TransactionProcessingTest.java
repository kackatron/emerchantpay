package com.payment.system.trx;


import com.payment.system.controllers.TransactionManagementController;
import com.payment.system.dao.models.trx.*;
import com.payment.system.dao.repositories.trx.TransactionRepository;
import com.payment.system.payload.request.CustomerInfo;
import com.payment.system.payload.request.RegisterTransaction;
import com.payment.system.security.UserDetailsImpl;
import com.payment.system.services.trx.*;
import com.payment.system.services.user.UserManagementService;
import com.payment.system.dao.models.User;
import com.payment.system.dao.repositories.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TransactionProcessingTest {

    @Autowired
    TransactionRegistrationService transactionRegistrationService;

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionProcessingService transactionProcessingService;

    @Autowired
    TransactionCleanupService transactionCleanupService;

    @Autowired
    TransactionRetrievalService transactionRetrievalService;


    private User testUser;

    private CustomerInfo customerInfo;

    //Tests a loading of Users from given csv.
    @BeforeEach
    public void setup() {
        userManagementService.setCsvFile("classpath:TestUsers.csv");
        testUser = userManagementService.loadUsers().get(0);
        assertNotNull(testUser, "Loading of user failed, it returned Null");
        // This is the only user in TestUsers.csv, on later date make this comparison dynamic.
        assertEquals("Test", testUser.getName());
        assertEquals("test@gmai.com", testUser.getEmail());

        customerInfo = new CustomerInfo("Customer1", "999-999-999");
    }

    @Test
    public void processHappyPath() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, TransactionManagementController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);
        RegisterTransaction registerChargeTransaction =
                new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                        TransactionManagementController.CHARGE, null);
        transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);

        transactionProcessingService.processTransactions();

        testUser = userRepository.findByName(testUser.getName()).orElseThrow(() -> new TransactionProcessingException("Can not find the test user"));
        assertEquals(testUser.getTotalTransactionSum(), authorizeTransaction.getAmount(),
                "The total amount of the merchant is different from the amount in transactions");
    }

    @Test
    public void processRevert() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, TransactionManagementController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

        RegisterTransaction registerChargeTransaction =
                new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                        TransactionManagementController.CHARGE, null);
        ChargeTransaction chargeTransaction = transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);

        RegisterTransaction registerRefundTransaction = new RegisterTransaction("3", "100.",
                chargeTransaction.getUuid().toString(), TransactionManagementController.REFUND, null);
        transactionRegistrationService.registerRefundTransaction(registerRefundTransaction);

        transactionProcessingService.processTransactions();

        testUser = userRepository.findByName(testUser.getName()).orElseThrow(() -> new TransactionProcessingException("Can not find the test user"));
        assertEquals(0.0, testUser.getTotalTransactionSum(),
                "The total amount of the merchant is different from the amount in transactions");
    }

    @Test
    public void ProcessReverse() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, TransactionManagementController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

        RegisterTransaction registerReverseTransaction = new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                TransactionManagementController.REVERSAL, null);
        transactionRegistrationService.registerReversalTransaction(registerReverseTransaction);
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        transactionProcessingService.processTransactions();
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));
        assertEquals(ETrxStatus.REVERSED, authorizeTransaction.getStatus(), "Authorization transaction is not in reversed state after registering a reversal transaction.");
    }

    @Test
    public void ProcessReverseAfterCharge() throws TransactionProcessingException {
        RegisterTransaction registerAuthorizationTransaction =
                new RegisterTransaction("1", "100.0", null, TransactionManagementController.AUTHORIZATION, customerInfo);
        AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

        RegisterTransaction registerChargeTransaction =
                new RegisterTransaction("2", "100.0", authorizeTransaction.getUuid().toString(),
                        TransactionManagementController.CHARGE, null);
        transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);

        RegisterTransaction registerReverseTransaction = new RegisterTransaction("3", "100.0",
                authorizeTransaction.getUuid().toString(), TransactionManagementController.REVERSAL, null);

        ReversalTransaction reversalTransaction = transactionRegistrationService.registerReversalTransaction(registerReverseTransaction);
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        transactionProcessingService.processTransactions();
        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        authorizeTransaction = (AuthorizeTransaction) transactionRepository.findByUuid(authorizeTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the authorize transaction"));

        reversalTransaction = (ReversalTransaction) transactionRepository.findByUuid(reversalTransaction
                .getUuid()).orElseThrow(() -> new TransactionProcessingException("Can not find the reversal transaction"));

        assertEquals(ETrxStatus.ERROR, reversalTransaction.getStatus(), "Authorization transaction is reversed but it was already charged.");
        assertEquals(ETrxStatus.APPROVED, authorizeTransaction.getStatus(), "Authorization transaction is reversed but it was already charged.");
    }

    @Test
    public void retrieveTransactionsForAnUser() throws TransactionProcessingException, TransactionRetrievalException {
        int transactionCount = 10;
        for (int trxUid = 1; trxUid <= transactionCount; trxUid++) {
            RegisterTransaction registerAuthorizationTransaction =
                    new RegisterTransaction(String.valueOf(trxUid), "100.0", null, TransactionManagementController.AUTHORIZATION, customerInfo);
            AuthorizeTransaction authorizeTransaction = transactionRegistrationService
                    .registerAuthorizationTransaction(UserDetailsImpl.getUserDetailsImpl(testUser), registerAuthorizationTransaction);

            RegisterTransaction registerChargeTransaction =
                    new RegisterTransaction(String.valueOf(++trxUid), "100.0", authorizeTransaction.getUuid().toString(),
                            TransactionManagementController.CHARGE, null);
            transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);
        }

        List<Transaction> transactionsOfTestUser = transactionRetrievalService.retrieveTransactionsForUser(UserDetailsImpl.getUserDetailsImpl(testUser));
        assertEquals(10, transactionsOfTestUser.size(), "Transactions for the user are not the expected count.");
        transactionRepository.deleteAll(transactionsOfTestUser);
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
