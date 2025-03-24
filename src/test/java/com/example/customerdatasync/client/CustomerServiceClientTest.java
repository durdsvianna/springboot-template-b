package com.example.customerdatasync.client;

import com.example.customerdatasync.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClientMutated;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CustomerServiceClient customerServiceClient;

    private final String customerServiceUrl = "http://localhost:8081/api/v1/customer";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(customerServiceClient, "customerServiceUrl", customerServiceUrl);
        
        // Setup WebClient mock chain for mutate() pattern
        when(webClient.mutate()).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMutated);
        when(webClientMutated.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getCustomers_shouldReturnCustomerList_whenServiceReturnsCustomers() {
        // Arrange
        Customer customer1 = Customer.builder().id(1L).name("John Doe").email("john@example.com").build();
        Customer customer2 = Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build();
        List<Customer> expectedCustomers = Arrays.asList(customer1, customer2);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedCustomers));

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
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(Collections.emptyList()));

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenServiceThrowsWebClientResponseException() {
        // Arrange
        WebClientResponseException exception = Mockito.mock(WebClientResponseException.class);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(exception));

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }

    @Test
    void getCustomers_shouldReturnEmptyList_whenServiceThrowsGenericException() {
        // Arrange
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        // Act
        List<Customer> actualCustomers = customerServiceClient.getCustomers();

        // Assert
        assertThat(actualCustomers).isNotNull();
        assertThat(actualCustomers).isEmpty();
    }
} 