package com.payment.system.dao.models.trx;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * RefundTransaction is transaction that can revert{@link ChargeTransaction}. It removes the amount from merchants
 * account ant restores it to the customer.
 */
@Entity
@DiscriminatorValue("REFUND")
public class RefundTransaction extends Transaction {

    public RefundTransaction(){}
    public RefundTransaction(Long uuid, ChargeTransaction reference_id, @NotBlank @Min(1) double amount) {
        this.uuid=uuid;
        this.reference_id = reference_id;
        this.amount = amount;
        this.merchant = reference_id.getMerchant();
    }

    @Override
    public String toString() {
        return "RefundTransaction{" +
                "reference_id=" + reference_id +
                ", amount=" + amount +
                ", uuid=" + uuid +
                ", dateTimeCreation=" + dateTimeCreation +
                ", status=" + status +
                ", merchant=" + merchant +
                '}';
    }
}
