package com.example.customerdatasync.service;

import com.example.customerdatasync.client.CustomerServiceClient;
import com.example.customerdatasync.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSyncServiceTest {

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private KafkaPublisherService kafkaPublisherService;

    @InjectMocks
    private CustomerSyncService customerSyncService;

    @Test
    void syncCustomers_shouldPublishCustomers_whenCustomersExist() {
        // Arrange
        Customer customer1 = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        Customer customer2 = Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build();
        List<Customer> customers = Arrays.asList(customer1, customer2);
        
        when(customerServiceClient.getCustomers()).thenReturn(customers);
        
        // Act
        customerSyncService.syncCustomers();
        
        // Assert
        verify(customerServiceClient).getCustomers();
        verify(kafkaPublisherService).publishCustomers(customers);
    }

    @Test
    void syncCustomers_shouldNotPublishCustomers_whenNoCustomersExist() {
        // Arrange
        when(customerServiceClient.getCustomers()).thenReturn(Collections.emptyList());
        
        // Act
        customerSyncService.syncCustomers();
        
        // Assert
        verify(customerServiceClient).getCustomers();
        verifyNoInteractions(kafkaPublisherService);
    }
} 