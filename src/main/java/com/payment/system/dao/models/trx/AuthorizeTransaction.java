package com.payment.system.dao.models.trx;

import com.payment.system.dao.models.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue("AUTH")
public class AuthorizeTransaction extends Transaction {
    @NotBlank
    @Min(1)
    private Long amount;

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
                                @NotBlank @Min(1) long amount
    ) {
        this.uuid = uuid;
        this.customer_email = customer_email;
        this.customer_phone = customer_phone;
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
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

}
