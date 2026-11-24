
package com.banking.dto;

import java.time.LocalDateTime;

public class CustomerResponse {

    private Long customerId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    public CustomerResponse() {
    }

    public CustomerResponse(
            Long customerId,
            String fullName,
            String email,
            String phone,
            LocalDateTime createdAt) {

        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

