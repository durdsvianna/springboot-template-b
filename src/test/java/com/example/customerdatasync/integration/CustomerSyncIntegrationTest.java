package com.example.customerdatasync.integration;

import com.example.customerdatasync.client.CustomerServiceClient;
import com.example.customerdatasync.model.Customer;
import com.example.customerdatasync.service.KafkaPublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "CLIENTES_HOJE")
class CustomerSyncIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceClient customerServiceClient;

    @MockBean
    private KafkaPublisherService kafkaPublisherService;

    @Test
    void syncCustomersEndpoint_shouldSyncCustomers() throws Exception {
        // Arrange
        Customer customer1 = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        Customer customer2 = Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build();
        List<Customer> customers = Arrays.asList(customer1, customer2);
        
        when(customerServiceClient.getCustomers()).thenReturn(customers);
        doNothing().when(kafkaPublisherService).publishCustomers(customers);

        // Act & Assert
        mockMvc.perform(post("/v1/sync/customers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("completed successfully")));

        verify(customerServiceClient).getCustomers();
        verify(kafkaPublisherService).publishCustomers(customers);
    }

    @Test
    void syncCustomersEndpoint_shouldHandleErrors() throws Exception {
        // Arrange
        when(customerServiceClient.getCustomers()).thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        mockMvc.perform(post("/v1/sync/customers"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error during customer sync")));

        verify(customerServiceClient).getCustomers();
        verifyNoInteractions(kafkaPublisherService);
    }
} 