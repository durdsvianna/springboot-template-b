package com.example.customerdatasync.service;

import com.example.customerdatasync.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisherService {

    private final KafkaTemplate<String, Customer> kafkaTemplate;
    private static final String TOPIC = "CLIENTES_HOJE";

    public void publishCustomers(List<Customer> customers) {
        log.info("Publishing {} customers to Kafka topic {}", customers.size(), TOPIC);
        
        customers.forEach(customer -> {
            CompletableFuture<SendResult<String, Customer>> future = kafkaTemplate.send(TOPIC, String.valueOf(customer.getId()), customer);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Customer with ID {} sent to topic {}, partition {}, offset {}",
                            customer.getId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send customer with ID {} to topic {}: {}",
                            customer.getId(), TOPIC, ex.getMessage(), ex);
                }
            });
        });
    }
} 