package com.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {

    private Long accountId;
    private Long customerId;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;

    public AccountResponse(
            Long accountId,
            Long customerId,
            String accountNumber,
            BigDecimal balance,
            String status,
            LocalDateTime createdAt) {

        this.accountId = accountId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
