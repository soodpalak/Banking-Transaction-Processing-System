package com.banking.controller;

import com.banking.dto.AccountRequest;
import com.banking.dto.AccountResponse;
import com.banking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(
            @PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @Valid @RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/customer/{customerId}")
    public List<AccountResponse> getAccountsByCustomerId(
            @PathVariable Long customerId) {
        return accountService.getAccountsByCustomerId(customerId);
    }
}
