package com.example.customerdatasync.client;

import com.example.customerdatasync.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CustomerServiceClient customerServiceClient;

    private final String customerServiceUrl = "http://localhost:8081/api/v1/customer";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(customerServiceClient, "customerServiceUrl", customerServiceUrl);
    }

    @Test
    void getCustomers_shouldReturnCustomerList_whenServiceReturnsCustomers() {
        // Arrange
        Customer customer1 = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        Customer customer2 = Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build();
        List<Customer> expectedCustomers = Arrays.asList(customer1, customer2);

        ResponseEntity<List<Customer>> responseEntity = new ResponseEntity<>(expectedCustomers, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(customerServiceUrl),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).hasSize(2);
        assertThat(actualCustomers).containsExactlyElementsOf(expectedCustomers);
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenServiceReturnsEmptyList() {
        // Arrange
        ResponseEntity<List<Customer>> responseEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        when(restTemplate.exchange(
                eq(customerServiceUrl),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenServiceThrowsException() {
        // Arrange
        when(restTemplate.exchange(
                eq(customerServiceUrl),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("Service unavailable"));

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }
} 