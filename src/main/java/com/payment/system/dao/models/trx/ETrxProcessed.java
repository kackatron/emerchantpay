package com.payment.system.dao.models.trx;

/**
 * Technical status of transaction, shows if TransactionProcessinsService has already calculated this trx and
 * respectively if this transaction is eligible  for deletion after a given period of time
 */
public enum ETrxProcessed {
    PROCESSED,
    PENDING
}
