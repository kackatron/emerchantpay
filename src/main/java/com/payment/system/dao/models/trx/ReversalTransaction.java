package com.payment.system.dao.models.trx;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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
}
