package com.andrey.notificationhub.job;

import com.andrey.notificationhub.service.LoanStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OverdueLoansJob {
    private final LoanStatusService service;

    @Scheduled(cron = "0 0 0 * * *")
    public void execute(){
        service.updateOverdueLoans(LocalDate.now());
    }
}
