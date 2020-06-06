package com.payment.system.dao.models.trx;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
    public RefundTransaction(Long uuid, ChargeTransaction reference_id, @NotBlank @Min(1) Long amount) {
        this.uuid=uuid;
        this.reference_id = reference_id;
        this.amount = amount;
    }

    @OneToOne
    @JoinColumn(name = "uuid")
    protected ChargeTransaction reference_id;

    @NotBlank
    @Min(1)
    private Long amount;

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
