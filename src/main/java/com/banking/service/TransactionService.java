package com.banking.service;
import com.banking.dto.DepositRequest;
import com.banking.dto.WithdrawalRequest;
import com.banking.dto.TransactionResponse;
import com.banking.exception.DuplicateResourceException;
import org.springframework.transaction.annotation.Transactional;
import com.banking.repository.AccountRepository;
import com.banking.repository.BankTransactionRepository;
import org.springframework.stereotype.Service;
import com.banking.entity.Account;
import com.banking.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import com.banking.entity.BankTransaction;
import java.time.LocalDateTime;
@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            BankTransactionRepository bankTransactionRepository) {

        this.accountRepository = accountRepository;
        this.bankTransactionRepository = bankTransactionRepository;
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {

        if (bankTransactionRepository.existsById(
                request.getTransactionId())) {

            throw new DuplicateResourceException(
                    "Transaction ID already exists");
        }
        Account account = accountRepository
                .findByIdForUpdate(request.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException(
                    "Account is not active");
        }
        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero");
        }
        BigDecimal updatedBalance = account.getBalance()
                .add(request.getAmount());

        account.setBalance(updatedBalance);

        accountRepository.save(account);
        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setType("DEPOSIT");
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setDestinationAccount(account);

        bankTransactionRepository.save(transaction);
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                null,
                account.getAccountId(),
                LocalDateTime.now()
        );
    }


    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request) {

        // 1. Check duplicate Transaction ID
        if (bankTransactionRepository.existsById(
                request.getTransactionId())) {
            throw new DuplicateResourceException(
                    "Transaction ID already exists");
        }

        // 2. Lock and fetch account
        Account account = accountRepository
                .findByIdForUpdate(request.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        // 3. Check account status
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException(
                    "Account is not active");
        }

        // 4. Check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance");
        }

        // 5. Deduct withdrawal amount
        BigDecimal updatedBalance = account.getBalance()
                .subtract(request.getAmount());

        account.setBalance(updatedBalance);
        accountRepository.save(account);

        // 6. Create transaction record
        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setType("WITHDRAWAL");
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setSourceAccount(account);

        bankTransactionRepository.save(transaction);

        // 7. Return response
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                account.getAccountId(),
                null,
                LocalDateTime.now()
        );
    }

}
