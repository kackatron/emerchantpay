package com.payment.system.dao;

import com.payment.system.dao.models.User;
import com.payment.system.dao.models.trx.AuthorizeTransaction;
import com.payment.system.dao.models.trx.ETrxStatus;
import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.dao.repositories.TransactionRepository;
import com.payment.system.dao.repositories.UserRepository;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
public class TransactionRepositoryTest {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;

    static User merchant;

    @BeforeEach
    public void setup(){
        if (merchant == null){
            merchant = new User("Mr. Smith","matrix@gmail.com","password");
            userRepository.save(merchant);
        }
    }
    @Test
    void testDaoAuthorizationTransaction(){
        long authTrUid = 1;
        assertNotNull("Transaction Repository didn`t wire.",transactionRepository);
        transactionRepository.save(new AuthorizeTransaction(authTrUid,"asd@asd.asd","112",100));
        assertTrue("Can not confirm the existence of transaction just created. ",transactionRepository.existsById(authTrUid));
        Transaction authorizeTransaction = transactionRepository.findByUuid(authTrUid).orElseThrow(() -> new RuntimeException("Error: Transaction is missing"));
        assertNotNull("Can not find a transaction just creadted",authorizeTransaction);
        authorizeTransaction.setStatus(ETrxStatus.APPROVED);
        transactionRepository.save(authorizeTransaction);
        authorizeTransaction.setMerchant(merchant);
        transactionRepository.save(authorizeTransaction);
        transactionRepository.delete(authorizeTransaction);
    }

    @AfterEach
    public void cleanup(){
        if (merchant!=null) {
            userRepository.delete(merchant);
        }
    }
}
