package com.example.customerdatasync.client;

import com.example.customerdatasync.model.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerServiceClientTest {

    private MockWebServer mockWebServer;
    private CustomerServiceClient customerServiceClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .build();
        
        objectMapper = new ObjectMapper();
        customerServiceClient = new CustomerServiceClient(webClient);
        
        // Set the base URL to point to our mock server
        String url = String.format("http://localhost:%s", mockWebServer.getPort());
        ReflectionTestUtils.setField(customerServiceClient, "customerServiceUrl", url);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getCustomers_shouldReturnCustomerList_whenServiceReturnsCustomers() throws JsonProcessingException {
        // Arrange
        Customer customer1 = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        Customer customer2 = Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build();
        List<Customer> expectedCustomers = Arrays.asList(customer1, customer2);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(expectedCustomers))
        );

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).hasSize(2);
        assertThat(actualCustomers.get(0).getId()).isEqualTo(1L);
        assertThat(actualCustomers.get(0).getName()).isEqualTo("John Doe");
        assertThat(actualCustomers.get(1).getId()).isEqualTo(2L);
        assertThat(actualCustomers.get(1).getName()).isEqualTo("Jane Doe");
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenServiceReturnsEmptyList() throws JsonProcessingException {
        // Arrange
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("[]")
        );

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenServiceThrowsError() {
        // Arrange
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"error\": \"Internal Server Error\"}")
        );

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }
} 