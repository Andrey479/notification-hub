package com.andrey.notificationhub.service;

import com.andrey.notificationhub.model.Loan;
import com.andrey.notificationhub.model.LoanStatus;
import com.andrey.notificationhub.repository.LoanRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class LoanStatusService {

    private final LoanRepository loanRepository;

    public int updateOverdueLoans(LocalDate referenceDate){
        List<Loan>  loanList = loanRepository.findByStatusAndExpectedReturnDateBefore(LoanStatus.ACTIVE, referenceDate);
        int count = 0;
        for (Loan loan : loanList) {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);
            count++;
        }
        return count;
    }
}
