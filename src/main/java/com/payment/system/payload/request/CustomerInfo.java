package com.payment.system.payload.request;

public class CustomerInfo {
    String customer_email;
    String customer_phone;

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    @Override
    public String toString() {
        return "CustomerInfo{" +
                "customer_email='" + customer_email + '\'' +
                ", customer_phone='" + customer_phone + '\'' +
                '}';
    }
}
