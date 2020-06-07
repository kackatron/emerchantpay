package com.payment.system.services.trx;

import com.payment.system.dao.models.User;
import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.dao.repositories.trx.TransactionRepository;
import com.payment.system.dao.repositories.user.UserRepository;
import com.payment.system.security.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service that covers the retrieving of transactions.
 */
@Service
@Transactional
public class TransactionRetrievalService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRetrievalService.class);
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    public List<Transaction> retrieveTransactionsForUser(UserDetailsImpl userDetails) throws TransactionRetrievalException {
        logger.info("Retrieving transactions for user {} ", userDetails);
        User user = userRepository.findByName(userDetails.getUsername()).orElseThrow(()->new TransactionRetrievalException("Can not find user with name " + userDetails.getUsername()) );
        List<Transaction> merchantTransactions = transactionRepository.findAllForUser(user);
        return  merchantTransactions;
    }
}
