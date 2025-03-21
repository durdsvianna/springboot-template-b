package com.example.customerdatasync.client;

import com.example.customerdatasync.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.customer-service.url}")
    private String customerServiceUrl;

    public List<Customer> getCustomers() {
        try {
            log.info("Fetching customers from {}", customerServiceUrl);
            ResponseEntity<List<Customer>> response = restTemplate.exchange(
                    customerServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Customer>>() {});
            
            List<Customer> customers = response.getBody();
            log.info("Fetched {} customers", customers != null ? customers.size() : 0);
            return customers != null ? customers : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching customers from {}: {}", customerServiceUrl, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
} 