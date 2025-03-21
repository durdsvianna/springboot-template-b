package com.example.customerdatasync.client;

import com.example.customerdatasync.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceClient {

    private final WebClient webClient;

    @Value("${app.customer-service.url}")
    private String customerServiceUrl;

    public List<Customer> getCustomers() {
        try {
            log.info("Fetching customers from {}", customerServiceUrl);
            List<Customer> customers = webClient.get()
                    .uri(customerServiceUrl)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Customer>>() {})
                    .block();
            
            log.info("Fetched {} customers", customers != null ? customers.size() : 0);
            return customers != null ? customers : Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error("Error response from {}: {} - {}", customerServiceUrl, e.getStatusCode(), e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching customers from {}: {}", customerServiceUrl, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
} 