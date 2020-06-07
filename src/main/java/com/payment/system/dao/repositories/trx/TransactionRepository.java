package com.payment.system.dao.repositories.trx;

import com.payment.system.dao.models.trx.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByUuid(long uuid);
    @Override
    List<Transaction> findAll(Sort sort);

    @Query("SELECT t FROM Transaction t WHERE t.processed = 0 and t.dateTimeCreation < ?1")
    Collection<Transaction> findAllProcessedTransactionsOlderThan(Date date);
}
