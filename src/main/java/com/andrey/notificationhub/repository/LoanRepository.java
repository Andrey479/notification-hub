package com.andrey.notificationhub.repository;

import com.andrey.notificationhub.model.Loan;
import com.andrey.notificationhub.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatusAndExpectedReturnDateBefore(LoanStatus status, LocalDate referenceDate);
}
