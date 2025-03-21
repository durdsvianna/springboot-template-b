package com.example.customerdatasync.controller;

import com.example.customerdatasync.service.CustomerSyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSyncControllerTest {

    @Mock
    private CustomerSyncService customerSyncService;

    @InjectMocks
    private CustomerSyncController customerSyncController;

    @Test
    void syncCustomers_shouldReturnSuccess_whenSyncCompletesSuccessfully() {
        // Arrange
        doNothing().when(customerSyncService).syncCustomers();

        // Act
        ResponseEntity<String> response = customerSyncController.syncCustomers();

        // Assert
        verify(customerSyncService).syncCustomers();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("completed successfully");
    }

    @Test
    void syncCustomers_shouldReturnError_whenSyncThrowsException() {
        // Arrange
        String errorMessage = "Service unavailable";
        doThrow(new RuntimeException(errorMessage)).when(customerSyncService).syncCustomers();

        // Act
        ResponseEntity<String> response = customerSyncController.syncCustomers();

        // Assert
        verify(customerSyncService).syncCustomers();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains(errorMessage);
    }
} 