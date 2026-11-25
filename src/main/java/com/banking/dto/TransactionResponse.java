
package com.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private String transactionId;
    private String type;
    private BigDecimal amount;
    private String status;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private LocalDateTime createdAt;

    public TransactionResponse(
            String transactionId,
            String type,
            BigDecimal amount,
            String status,
            Long sourceAccountId,
            Long destinationAccountId,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

