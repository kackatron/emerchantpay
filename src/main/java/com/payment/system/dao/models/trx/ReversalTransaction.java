package com.payment.system.dao.models.trx;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * ReversalTransaction is transaction that cancels {@link AuthorizeTransaction}
 */
@Entity
@DiscriminatorValue("REVERSE")
public class ReversalTransaction extends Transaction {

    public ReversalTransaction(){ }
    public ReversalTransaction(Long uuid, AuthorizeTransaction reference_id) {
        this.uuid = uuid;
        this.reference_id = reference_id;
    }


    public AuthorizeTransaction getReference_id() {
        return reference_id;
    }

    public void setReference_id(AuthorizeTransaction reference_id) {
        this.reference_id = reference_id;
    }

    @OneToOne
    @JoinColumn(name = "uuid")
    protected AuthorizeTransaction reference_id;

    @Override
    public String toString() {
        return "ReversalTransaction{" +
                "reference_id=" + reference_id +
                ", uuid=" + uuid +
                ", dateTimeCreation=" + dateTimeCreation +
                ", status=" + status +
                ", merchant=" + merchant +
                '}';
    }
}
