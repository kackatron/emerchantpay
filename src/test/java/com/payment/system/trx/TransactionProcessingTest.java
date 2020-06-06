package com.payment.system.trx;


import com.payment.system.controllers.LoadTransactionController;
import com.payment.system.dao.models.trx.AuthorizeTransaction;
import com.payment.system.dao.models.trx.ChargeTransaction;
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
        ChargeTransaction chargeTransaction = transactionRegistrationService.registerChargeTransaction(registerChargeTransaction);
        // This is scheduled job but to avoid ambiguity we call it manually
        // TODO if there is time test the scheduling
        transactionProcessingService.processTransactions();
        testUser = userRepository.findByName(testUser.getName()).orElseThrow(() -> new TransactionProcessingException("Ca not find the test user"));
        assertEquals(testUser.getTotalTransactionSum(),authorizeTransaction.getAmount(),
                "The total amount of the merchant is different from the amount in transactions");
    }

    @Test
    public void processRevert() {

    }

    @Test
    public void ProcessReverse() {

    }

    @AfterEach
    public void tearDown() {
        transactionCleanupService.setTrxAge(0);
        transactionCleanupService.cleanTransactions();
        userRepository.delete(testUser);
    }
}
