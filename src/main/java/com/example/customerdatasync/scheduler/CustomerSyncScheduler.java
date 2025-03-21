package com.example.customerdatasync.scheduler;

import com.example.customerdatasync.service.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true")
public class CustomerSyncScheduler {

    private final CustomerSyncService customerSyncService;

    @Scheduled(cron = "${app.scheduler.customer-sync-cron}")
    public void syncCustomers() {
        log.info("Scheduled customer sync task started");
        customerSyncService.syncCustomers();
        log.info("Scheduled customer sync task completed");
    }
} 