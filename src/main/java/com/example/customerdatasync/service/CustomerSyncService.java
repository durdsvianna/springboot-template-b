package com.example.customerdatasync.service;

import com.example.customerdatasync.client.CustomerServiceClient;
import com.example.customerdatasync.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerSyncService {

    private final CustomerServiceClient customerServiceClient;
    private final KafkaPublisherService kafkaPublisherService;

    public void syncCustomers() {
        log.info("Starting customer sync process");
        List<Customer> customers = customerServiceClient.getCustomers();
        
        if (customers.isEmpty()) {
            log.warn("No customers found to sync");
            return;
        }
        
        log.info("Found {} customers to sync", customers.size());
        kafkaPublisherService.publishCustomers(customers);
        log.info("Customer sync process completed");
    }
} 