
package com.banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTION_AUDIT")
public class TransactionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private BankTransaction transaction;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_timestamp", insertable = false, updatable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "thread_name", nullable = false, length = 100)
    private String threadName;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "details", length = 500)
    private String details;

    public TransactionAudit() {
    }

    public Long getAuditId() {
        return auditId;
    }

    public BankTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(BankTransaction transaction) {
        this.transaction = transaction;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}


