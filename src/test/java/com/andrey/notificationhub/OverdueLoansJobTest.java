package com.andrey.notificationhub;

import com.andrey.notificationhub.job.OverdueLoansJob;
import com.andrey.notificationhub.service.LoanStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OverdueLoansJobTest {

    @Mock
    LoanStatusService service;

    @InjectMocks
    OverdueLoansJob job;

    @Test
    void shouldTriggerTheServiceSuccessfully(){
        job.execute();

        verify(service).updateOverdueLoans(any(LocalDate.class));
    }
}
