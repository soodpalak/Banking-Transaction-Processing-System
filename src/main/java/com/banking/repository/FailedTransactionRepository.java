
package com.banking.repository;

import com.banking.entity.FailedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailedTransactionRepository
        extends JpaRepository<FailedTransaction, Long> {

    List<FailedTransaction> findByErrorCategory(String errorCategory);
}

