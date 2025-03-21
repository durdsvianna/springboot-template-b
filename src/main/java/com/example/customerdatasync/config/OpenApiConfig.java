package com.example.customerdatasync.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Customer Data Sync API")
                        .version("1.0")
                        .description("API that consumes customer data and publishes to Kafka")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com")));
    }
} 