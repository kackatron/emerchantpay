package com.payment.system.dao.models.trx;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AuthorizeTransaction is transaction the verifies that desired amount is available on customers account and then blocks it.
 */
@Entity
@DiscriminatorValue("AUTH")
public class AuthorizeTransaction extends Transaction {
    @NotBlank
    @Min(1)
    private double amount;

    @Size(max = 50)
    @Email
    private String customer_email;

    @Size(max = 20)
    private String customer_phone;


    public AuthorizeTransaction(){
    }

    public AuthorizeTransaction(@NotBlank @Min(1) long uuid,
                                @Size(max = 50) @Email String customer_email,
                                @Size(max = 20) String customer_phone,
                                @NotBlank @Min(1) double amount
    ) {
        this.uuid = uuid;
        this.customer_email = customer_email;
        this.customer_phone = customer_phone;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

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
        return "AuthorizeTransaction{" +
                "amount=" + amount +
                ", customer_email='" + customer_email + '\'' +
                ", customer_phone='" + customer_phone + '\'' +
                ", uuid=" + uuid +
                ", dateTimeCreation=" + dateTimeCreation +
                ", status=" + status +
                ", merchant=" + merchant +
                '}';
    }
}
