
package com.banking.service;

import com.banking.dto.CustomerRequest;
import com.banking.dto.CustomerResponse;
import com.banking.entity.Customer;
import com.banking.repository.CustomerRepository;
import com.banking.exception.DuplicateResourceException;
import com.banking.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + id));

        return convertToResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (customerRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer savedCustomer = customerRepository.save(customer);

        return convertToResponse(savedCustomer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + id));

        customerRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getCustomerId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Email already exists");
                });

        customerRepository.findByPhone(request.getPhone())
                .filter(existing -> !existing.getCustomerId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Phone number already exists");
                });

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updatedCustomer = customerRepository.save(customer);

        return convertToResponse(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + id));

        customerRepository.delete(customer);
    }

    private CustomerResponse convertToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt()
        );
    }
}
