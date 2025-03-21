package com.example.customerdatasync.service;

import com.example.customerdatasync.model.Customer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherServiceTest {

    @Mock
    private KafkaTemplate<String, Customer> kafkaTemplate;

    @InjectMocks
    private KafkaPublisherService kafkaPublisherService;

    @Test
    void publishCustomers_shouldPublishAllCustomers() {
        // Arrange
        Customer customer1 = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        Customer customer2 = Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build();
        List<Customer> customers = Arrays.asList(customer1, customer2);

        ProducerRecord<String, Customer> producerRecord = new ProducerRecord<>("CLIENTES_HOJE", "1", customer1);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("CLIENTES_HOJE", 0), 0, 0, 0, 0, 0);
        SendResult<String, Customer> sendResult = new SendResult<>(producerRecord, recordMetadata);
        
        CompletableFuture<SendResult<String, Customer>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(anyString(), anyString(), any(Customer.class))).thenReturn(future);

        // Act
        kafkaPublisherService.publishCustomers(customers);

        // Assert
        verify(kafkaTemplate, times(2)).send(eq("CLIENTES_HOJE"), anyString(), any(Customer.class));
    }

    @Test
    void publishCustomers_shouldHandleExceptions() {
        // Arrange
        Customer customer = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        List<Customer> customers = List.of(customer);

        CompletableFuture<SendResult<String, Customer>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));
        
        when(kafkaTemplate.send(anyString(), anyString(), any(Customer.class))).thenReturn(future);

        // Act - this should not throw an exception
        kafkaPublisherService.publishCustomers(customers);

        // Assert
        verify(kafkaTemplate).send(eq("CLIENTES_HOJE"), eq("1"), eq(customer));
    }
} 