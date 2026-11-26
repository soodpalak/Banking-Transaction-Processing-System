package com.banking.service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.banking.dto.AccountRequest;
import com.banking.dto.AccountResponse;
import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.exception.DuplicateResourceException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public AccountService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository) {

        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with ID: " + id));

        return convertToResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {

        if (accountRepository.findByAccountNumber(
                request.getAccountNumber()).isPresent()) {

            throw new DuplicateResourceException(
                    "Account number already exists");
        }

        Customer customer = customerRepository.findById(
                        request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: "
                                + request.getCustomerId()));

        Account account = new Account();

        account.setCustomer(customer);
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        account.setStatus(
                request.getStatus() == null
                        || request.getStatus().isBlank()
                        ? "ACTIVE"
                        : request.getStatus()
        );

        Account savedAccount = accountRepository.saveAndFlush(account);
        entityManager.refresh(savedAccount);

        return convertToResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found with ID: " + customerId);
        }

        return accountRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private AccountResponse convertToResponse(Account account) {

        return new AccountResponse(
                account.getAccountId(),
                account.getCustomer().getCustomerId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }

}
