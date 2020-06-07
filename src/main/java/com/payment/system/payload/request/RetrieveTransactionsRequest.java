package com.payment.system.payload.request;

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
