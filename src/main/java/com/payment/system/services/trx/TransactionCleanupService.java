package com.payment.system.services.trx;

import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.dao.repositories.trx.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

@Service
public class TransactionCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessingService.class);

    //Time to live of transaction in milliseconds. Default is one hour.
    private static int trxAge = 60 * 60 * 60 * 1000;

    @Autowired
    TransactionRepository transactionRepository;

    public int getTrxAge() {
        return trxAge;
    }

    //Sets Time to live of transactions in milliseconds. Needed for test purposes.
    public void setTrxAge(int trxAge) {
        TransactionCleanupService.trxAge = trxAge;
    }

    @Scheduled
    @Transactional
    public void cleanTransactions() {
        Date olderThan = new Date(System.currentTimeMillis() - trxAge);
        Collection<Transaction> transactionsToBeDeleted = transactionRepository.findAllProcessedTransactionsOlderThan(olderThan);
        logger.info("All this transaction will be deleted : {}", transactionsToBeDeleted);
        transactionRepository.deleteAll(transactionsToBeDeleted);
        transactionsToBeDeleted = transactionRepository.findAllProcessedTransactionsOlderThan(olderThan);
        logger.info("Left transactions {}", transactionsToBeDeleted);
        assert transactionsToBeDeleted.isEmpty();
    }
}
