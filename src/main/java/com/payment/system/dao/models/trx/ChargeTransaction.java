package com.payment.system.dao.models.trx;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ChargeTransaction comes after {@link AuthorizeTransaction} and
 * draws the money from customer and sends them to the merchant.
 */
@Entity
@DiscriminatorValue("CHRG")
public class ChargeTransaction extends Transaction{

    @NotBlank
    @Min(1)
    private double amount;

    public ChargeTransaction(){
    }
    public ChargeTransaction(@NotBlank Long uuid, @NotBlank @Size(max = 40) AuthorizeTransaction reference_id, @NotBlank @Min(1) double amount) {
        this.uuid = uuid;
        this.reference_id = reference_id;
        this.amount = amount;
        this.merchant = reference_id.getMerchant();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ChargeTransaction{" +
                "amount=" + amount +
                ", reference_id=" + reference_id +
                ", uuid=" + uuid +
                ", dateTimeCreation=" + dateTimeCreation +
                ", status=" + status +
                ", merchant=" + merchant +
                '}';
    }
}
