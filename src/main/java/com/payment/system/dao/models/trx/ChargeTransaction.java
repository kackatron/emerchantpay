package com.payment.system.dao.models.trx;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@DiscriminatorValue("CHRG")
public class ChargeTransaction extends Transaction{

    @NotBlank
    @Min(1)
    private Long amount;

    @OneToOne
    @JoinColumn(name="uuid")
    private AuthorizeTransaction reference_id;

    public ChargeTransaction(){
    }
    public ChargeTransaction(@NotBlank @Size(max = 40) AuthorizeTransaction reference_id, @NotBlank @Min(1) Long amount) {
        this.reference_id = reference_id;
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Transaction getReference_id() {
        return reference_id;
    }

    public void setReference_id(AuthorizeTransaction reference_id) { this.reference_id = reference_id; }
}
