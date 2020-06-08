package com.payment.system.payload.request;

/**
 * Request for retrieving transactions. Currently it is not used to its full potential, since we  dont have a use case
 * for seeking only  transaction by constraints.
 */
public class RetrieveTransactionsRequest {

    //if not set it will return bulk ot trxs.It is the main case.
    String trxId;

    public RetrieveTransactionsRequest(String trxId) {
        this.trxId = trxId;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }
}
