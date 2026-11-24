
package com.banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAILED_TRANSACTION")
public class FailedTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "failure_id")
    private Long failureId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private BankTransaction transaction;

    @Column(name = "error_category", nullable = false, length = 50)
    private String errorCategory;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "failed_at", insertable = false, updatable = false)
    private LocalDateTime failedAt;

    public FailedTransaction() {
    }

    public Long getFailureId() {
        return failureId;
    }

    public BankTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(BankTransaction transaction) {
        this.transaction = transaction;
    }

    public String getErrorCategory() {
        return errorCategory;
    }

    public void setErrorCategory(String errorCategory) {
        this.errorCategory = errorCategory;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }
}

