package com.example.customerdatasync.controller;

import com.example.customerdatasync.service.CustomerSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Customer Sync", description = "APIs for customer data synchronization")
public class CustomerSyncController {

    private final CustomerSyncService customerSyncService;

    @PostMapping("/customers")
    @Operation(summary = "Manually trigger customer data synchronization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sync process successfully triggered"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> syncCustomers() {
        log.info("Manual customer sync requested");
        try {
            customerSyncService.syncCustomers();
            return ResponseEntity.ok("Customer sync process completed successfully");
        } catch (Exception e) {
            log.error("Error during manual customer sync: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error during customer sync: " + e.getMessage());
        }
    }
} 