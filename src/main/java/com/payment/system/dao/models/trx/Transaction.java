package com.payment.system.dao.models.trx;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.payment.system.dao.models.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * Transaction is a JPA entity representing a single table that houses all classes that extend Transaction class:
 * - AuthorizeTransaction
 * - ChargeTransaction
 * - RefundTransaction
 * - ReversalTransaction
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRX_TYPE")
@Table(name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "uuid")
        })
@JsonSerialize
public class Transaction {
    @Id
    @NotBlank
    @Min(1)
    protected Long uuid;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date dateTimeCreation;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    protected ETrxStatus status;


    @ManyToOne
    @JoinColumn(updatable=true)
    protected User merchant;

    @OneToOne
    @JoinColumn(updatable=true)
    protected Transaction reference_id;

    // ETrxProcessed marks if the TransactionProcessingService gone through this transaction. If it is processed its vailable for
    protected ETrxProcessed processed;

    public Transaction() {
        processed = ETrxProcessed.PENDING;
        dateTimeCreation = new Date();
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public Date getDateCreated() {
        return dateTimeCreation;
    }

    public void setDateCreated(Date dateTimeCreation) {
        this.dateTimeCreation = dateTimeCreation;
    }

    public ETrxStatus getStatus() {
        return status;
    }

    public void setStatus(ETrxStatus status) {
        this.status = status;
    }

    public User getMerchant() {
        return merchant;
    }

    public void setMerchant(User merchant) {
        this.merchant = merchant;
    }

    public ETrxProcessed getProcessed() {
        return processed;
    }

    public void setProcessed(ETrxProcessed processed) {
        this.processed = processed;
    }
    public Transaction getReference_id() {
        return reference_id;
    }

    public void setReference_id(Transaction reference_id) {
        this.reference_id = reference_id;
    }

}
