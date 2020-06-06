package com.payment.system.services.trx;

import com.payment.system.controllers.LoadTransactionController;
import com.payment.system.dao.models.User;
import com.payment.system.dao.models.trx.*;
import com.payment.system.dao.repositories.trx.TransactionRepository;
import com.payment.system.dao.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessingService.class);
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Processes Transactions, by order of their creation.
     * In case of success it marks them Processed, including their referential transactions, if there are such.
     * In case of failure it marks them Processed and sets their status to ERROR and does not change the referenced transactions,
     * if there are such.
     */
    @Scheduled
    public void processTransactions() {
        logger.info("Start processing transactions.");
        List<Transaction> transactionList = transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTimeCreation"));
        for (Transaction transaction : transactionList) {
            if (transaction.getProcessed() == ETrxProcessed.PENDING) {
                try {
                    logger.info("Processing transaction {}", transaction);
                    if (transaction instanceof ChargeTransaction) {
                        handleChargeTransaction((ChargeTransaction) transaction);
                    }
                    if (transaction instanceof RefundTransaction) {
                        handleRefundTransaction((RefundTransaction) transaction);
                    }
                    if (transaction instanceof ReversalTransaction) {
                        handleReversalTransaction((ReversalTransaction) transaction);
                    }
                } catch (TransactionProcessingException transactionProcessingException) {
                    logger.error("Processing of transaction {}  finished with error.", transaction, transactionProcessingException);
                    transaction.setStatus(ETrxStatus.ERROR);
                    transaction.setProcessed(ETrxProcessed.PROCESSED);
                    transactionRepository.save(transaction);
                }
            }
        }
        logger.info("Finish processing transaction.");
    }

    /**
     * Handles Charge Transaction. Charge Transaction gets the money that previously Authorize transaction reserved.
     * It ads its amount to merchant account.
     * In Case of success Charge transaction stays Approved, but is marked as Processed. Authorize transaction is also
     * marked as processed. So they will be removed at the next cleanup job.
     *
     * @param chargeTransaction - POJO representing charge transaction
     * @throws TransactionProcessingException - in case of Charge transaction with no Authorize transaction as a reference.
     *                                        in case of mismatching amount between Authorize transaction and Charge transaction.
     *                                        in case of reversed Authorize transaction.
     */
    private void handleChargeTransaction(ChargeTransaction chargeTransaction) throws TransactionProcessingException {
        logger.info("Process {} ", chargeTransaction);
        User user = chargeTransaction.getMerchant();
        Transaction transaction = chargeTransaction.getReference_id();
        AuthorizeTransaction authorizeTransaction;
        try {
            authorizeTransaction = (AuthorizeTransaction) transaction;
            if (authorizeTransaction == null) {
                throw new TransactionProcessingException(
                        String.format("Missing Authorization transaction  for Charge transaction %s", chargeTransaction));
            }
            if (authorizeTransaction.getAmount() != chargeTransaction.getAmount()) {
                throw new TransactionProcessingException(
                        String.format("Amount on Charge Transaction: %s is different from Authorize Transaction: %s",
                                chargeTransaction, authorizeTransaction));
            }
            if (authorizeTransaction.getStatus() == ETrxStatus.REVERSED || authorizeTransaction.getStatus() == ETrxStatus.ERROR) {
                throw new TransactionProcessingException(
                        String.format("Referenced Authorization Transaction %s is not viable for charging", authorizeTransaction));
            }
        } catch (ClassCastException castException) {
            throw new TransactionProcessingException(
                    String.format("Reference Transaction for Charge Transaction %s have to be Authorization transaction, but it is not it is : %s",
                            chargeTransaction, transaction), castException);
        }
        double userTransactionSum = user.getTotalTransactionSum();
        userTransactionSum = userTransactionSum + chargeTransaction.getAmount();
        user.setTotalTransactionSum(userTransactionSum);
        chargeTransaction.setProcessed(ETrxProcessed.PROCESSED);
        authorizeTransaction.setProcessed(ETrxProcessed.PROCESSED);
        // If tests prove you need it, you can case this in transactional service.
        transactionRepository.save(chargeTransaction);
        transactionRepository.save(authorizeTransaction);
        userRepository.save(user);
    }

    /**
     * Handles Refund transaction. Refund transaction reverts Charge transaction. The merchant account is
     * deceased with the amount a Charge transaction previously added. The money are returned to customer.
     * In case of success Charge transaction is marked as Refunded. And the two Charge and Refund Transactions are marked as processed,
     * aka they will be removed in the next cleanup job.
     *
     * @param refundTransaction - POJO representing the refund transaction
     * @throws TransactionProcessingException - in case of no reference charge transaction or
     *                                        - in case the amounts of charge transaction and refund transaction are different.
     */
    private void handleRefundTransaction(RefundTransaction refundTransaction) throws TransactionProcessingException {
        logger.info("Process {} ", refundTransaction);
        User user = refundTransaction.getMerchant();
        Transaction transaction = refundTransaction.getReference_id();
        ChargeTransaction chargeTransaction;
        try {
            chargeTransaction = (ChargeTransaction) transaction;
            if (chargeTransaction == null) {
                throw new TransactionProcessingException(
                        String.format("Missing Charge transaction for Refund transaction %s", refundTransaction));
            }
            if (refundTransaction.getAmount() != chargeTransaction.getAmount()) {
                throw new TransactionProcessingException(
                        String.format("Amount on Refund Transaction: %s is different from Charge Transaction: %s",
                                refundTransaction, chargeTransaction));
            }
        } catch (ClassCastException castException) {
            throw new TransactionProcessingException(
                    String.format("Reference Transaction for Refund Transaction %s have to be Charge transaction, but it is not it is : %s",
                            refundTransaction, transaction), castException);
        }
        double userTransactionSum = user.getTotalTransactionSum();
        userTransactionSum = userTransactionSum - refundTransaction.getAmount();
        if (userTransactionSum < 0.0) {
            throw new TransactionProcessingException(
                    String.format("Transaction can not be processed %s, balance of a merchant %s should not be negative", refundTransaction, user));
        }
        user.setTotalTransactionSum(userTransactionSum);
        chargeTransaction.setProcessed(ETrxProcessed.PROCESSED);
        refundTransaction.setProcessed(ETrxProcessed.PROCESSED);
        // If tests prove you need it, you can case this in transactional service.
        transactionRepository.save(chargeTransaction);
        transactionRepository.save(refundTransaction);
        userRepository.save(user);
    }

    /**
     * Handles Reverse Transaction. It cancels Authorization transaction and unblocks the money of a customer.
     * In case of success it will set Authorization transaction in reversed state, and mark both Reversal and Authorization
     * transaction as processed which will make them viable for cleanup.
     * @param reversalTransaction - POJO representing a ReversalTransaction
     * @throws TransactionProcessingException - in case of reference transaction other than Authorization transaction.
     *                                        - in case of reference transaction which is already reversed.
     */
    private void handleReversalTransaction(ReversalTransaction reversalTransaction) throws TransactionProcessingException {
        logger.info("Process {} ", reversalTransaction);
        Transaction transaction = reversalTransaction.getReference_id();
        AuthorizeTransaction authorizeTransaction;
        try {
            authorizeTransaction = (AuthorizeTransaction) transaction;
        } catch (ClassCastException castException) {
            throw new TransactionProcessingException(
                    String.format("Reference Transaction for Reversal Transaction %s have to be Authorization transaction, but it is not it is : %s",
                            reversalTransaction, transaction), castException);
        }
        if (authorizeTransaction.getStatus() == ETrxStatus.REVERSED ) {
            throw new TransactionProcessingException(
                    String.format("Reference Transaction for Reversal Transaction %s is already in irreversible state %s",
                            reversalTransaction, transaction));
        }
        authorizeTransaction.setStatus(ETrxStatus.REVERSED);
        authorizeTransaction.setProcessed(ETrxProcessed.PROCESSED);
        reversalTransaction.setProcessed(ETrxProcessed.PROCESSED);
        // If tests prove you need it, you can case this in transactional service.
        transactionRepository.save(reversalTransaction);
        transactionRepository.save(authorizeTransaction);
    }
}
