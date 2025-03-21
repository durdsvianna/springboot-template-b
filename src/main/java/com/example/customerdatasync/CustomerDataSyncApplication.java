package com.example.customerdatasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CustomerDataSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerDataSyncApplication.class, args);
    }
} 