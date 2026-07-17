package com.andrey.notificationhub;

import com.andrey.notificationhub.model.Loan;
import com.andrey.notificationhub.model.LoanStatus;
import com.andrey.notificationhub.repository.LoanRepository;
import com.andrey.notificationhub.service.LoanStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanStatusServiceTest {
    @Mock
    LoanRepository loanRepository;

    @InjectMocks
    LoanStatusService loanStatusService;

    @Test
    void shouldChangeStatusToOverdueWhenLoanIsActive () {
        Loan loan = Loan.builder()
                .id(1L)
                .expectedReturnDate(LocalDate.of(2026, 7, 15))
                .status(LoanStatus.ACTIVE)
                .build();

        List<Loan> loans = List.of(loan);

        when(loanRepository.findByStatusAndExpectedReturnDateBefore(LoanStatus.ACTIVE, LocalDate.of(2026, 8, 15)))
                .thenReturn(loans);

        int atualizedQuantity = loanStatusService.updateOverdueLoans(LocalDate.of(2026, 8, 15));

        //contexto, fazendo esse metodo
        assertEquals(LoanStatus.OVERDUE, loans.getFirst().getStatus());
        assertEquals(1, atualizedQuantity);
        verify(loanRepository).save(loan);

    }
}
