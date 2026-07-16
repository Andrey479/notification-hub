package com.andrey.notificationhub.repository;

import com.andrey.notificationhub.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    findByStatusAndExpectedReturnDateBefore();
}
