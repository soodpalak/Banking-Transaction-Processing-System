
package com.banking.controller;

import com.banking.dto.DepositRequest;
import com.banking.dto.TransactionResponse;
import com.banking.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.dto.WithdrawalRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request) {

        TransactionResponse response =
                transactionService.deposit(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody WithdrawalRequest request) {

        TransactionResponse response =
                transactionService.withdraw(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}

