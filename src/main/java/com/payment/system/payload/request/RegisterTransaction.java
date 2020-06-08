package com.payment.system.payload.request;


import javax.validation.constraints.NotBlank;

/**
 * Pojo representing request for registering a transaction.
 */
public class RegisterTransaction {
    @NotBlank
    private String uuid;

    @NotBlank
    private String amount;

    private String refTrx;
    @NotBlank
    private String typeOfTrx;

    public RegisterTransaction(){}

    public RegisterTransaction(@NotBlank String uuid, @NotBlank String amount, String refTrx, @NotBlank String typeOfTrx, CustomerInfo customerInfo) {
        this.uuid = uuid;
        this.amount = amount;
        this.refTrx = refTrx;
        this.typeOfTrx = typeOfTrx;
        this.customerInfo = customerInfo;
    }

    private CustomerInfo customerInfo;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRefTrx() {
        return refTrx;
    }

    public void setRefTrx(String refTrx) {
        this.refTrx = refTrx;
    }

    public String getTypeOfTrx() {
        return typeOfTrx;
    }

    public void setTypeOfTrx(String typeOfTrx) {
        this.typeOfTrx = typeOfTrx;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    @Override
    public String toString() {
        return "RegisterTransaction{" +
                "uuid='" + uuid + '\'' +
                ", amount='" + amount + '\'' +
                ", refTrx='" + refTrx + '\'' +
                ", typeOfTrx='" + typeOfTrx + '\'' +
                ", customerInfo=" + customerInfo +
                '}';
    }
}
