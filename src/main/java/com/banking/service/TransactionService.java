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
import com.banking.dto.TransferRequest;
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
        // Check duplicate transaction ID
        if (bankTransactionRepository.existsById(
                request.getTransactionId())) {

            throw new DuplicateResourceException(
                    "Transaction ID already exists");
        }

        // Lock and fetch account
        Account account = accountRepository
                .findByIdForUpdate(request.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        //  Check account status
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException(
                    "Account is not active");
        }

        //  Check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance");
        }

        //  Deduct withdrawal amount
        BigDecimal updatedBalance = account.getBalance()
                .subtract(request.getAmount());

        account.setBalance(updatedBalance);
        accountRepository.save(account);

        // Create transaction record
        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setType("WITHDRAWAL");
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setSourceAccount(account);

        bankTransactionRepository.save(transaction);

        //  Return response
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
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {




        // Check source and destination are different
        if (request.getSourceAccountId().equals(
                request.getDestinationAccountId())) {

            throw new IllegalArgumentException(
                    "Source and destination accounts cannot be same");
        }

        Account sourceAccount;
        Account destinationAccount;

        //  Lock accounts in ascending ID order
        if (request.getSourceAccountId() <
                request.getDestinationAccountId()) {

            sourceAccount = accountRepository
                    .findByIdForUpdate(request.getSourceAccountId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Source account not found"));

            destinationAccount = accountRepository
                    .findByIdForUpdate(request.getDestinationAccountId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Destination account not found"));

        } else {

            destinationAccount = accountRepository
                    .findByIdForUpdate(request.getDestinationAccountId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Destination account not found"));

            sourceAccount = accountRepository
                    .findByIdForUpdate(request.getSourceAccountId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Source account not found"));
        }
        // Check if transaction ID already exists
        var existingTransaction = bankTransactionRepository.findById(
                request.getTransactionId()
        );

        if (existingTransaction.isPresent()) {

            BankTransaction existing = existingTransaction.get();

            boolean sameRequest =
                    "TRANSFER".equals(existing.getType()) &&
                            existing.getAmount().compareTo(request.getAmount()) == 0 &&
                            existing.getSourceAccount().getAccountId()
                                    .equals(request.getSourceAccountId()) &&
                            existing.getDestinationAccount().getAccountId()
                                    .equals(request.getDestinationAccountId());

            if (sameRequest) {
                return new TransactionResponse(
                        existing.getTransactionId(),
                        existing.getType(),
                        existing.getAmount(),
                        existing.getStatus(),
                        existing.getSourceAccount().getAccountId(),
                        existing.getDestinationAccount().getAccountId(),
                        existing.getCreatedAt()
                );
            }

            throw new DuplicateResourceException(
                    "Transaction ID already exists with different details"
            );
        }

        // 4. Check both accounts are ACTIVE
        if (!"ACTIVE".equals(sourceAccount.getStatus()) ||
                !"ACTIVE".equals(destinationAccount.getStatus())) {

            throw new IllegalStateException(
                    "Both accounts must be active");
        }

        // 5. Check sufficient balance
        if (sourceAccount.getBalance().compareTo(
                request.getAmount()) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient balance");
        }

        // 6. Update both account balances
        BigDecimal updatedSourceBalance =
                sourceAccount.getBalance().subtract(request.getAmount());

        BigDecimal updatedDestinationBalance =
                destinationAccount.getBalance().add(request.getAmount());

        sourceAccount.setBalance(updatedSourceBalance);
        destinationAccount.setBalance(updatedDestinationBalance);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // 7. Create transaction record
        BankTransaction transaction = new BankTransaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setType("TRANSFER");
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);

        bankTransactionRepository.save(transaction);

        // 8. Return transaction response
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                sourceAccount.getAccountId(),
                destinationAccount.getAccountId(),
                LocalDateTime.now()
        );
    }

}
