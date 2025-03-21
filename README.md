# Customer Data Sync Service

A microservice that synchronizes customer data from an external customer service to Apache Kafka.

## Features

- Consumes the `listarClientes` method from an external customer microservice at `/api/v1/customer`
- Scheduled synchronization of customer data to a Kafka topic
- Manual trigger endpoint for customer data synchronization
- Comprehensive testing suite including unit, integration, and BDD tests

## Technologies

- Java 21
- Spring Boot 3.2.3
- Apache Kafka
- Lombok
- JUnit 5
- Cucumber (BDD testing)
- RestAssured
- SpringDoc OpenAPI (Swagger UI)

## Architecture

The application follows a clean architecture approach:

- **Controller Layer**: REST endpoints for manual triggering of the sync process
- **Service Layer**: Business logic for customer data synchronization
- **Client Layer**: External service communication
- **Scheduler**: Automatic triggering of the sync process
- **Kafka Integration**: Publishing data to Kafka topics

## Running the Application

### Prerequisites

- Java 21
- Maven
- Apache Kafka running on localhost:9092 (or configure in application.yml)

### Build and Run

```bash
# Build the application
mvn clean package

# Run the application
java -jar target/customer-data-sync-0.0.1-SNAPSHOT.jar
```

### Configuration

The application can be configured via `application.yml`:

```yaml
# Customer service configuration
app.customer-service.url: The URL of the customer service
app.customer-service.connect-timeout: Connection timeout in milliseconds
app.customer-service.read-timeout: Read timeout in milliseconds

# Scheduler configuration
app.scheduler.customer-sync-cron: Cron expression for scheduling the sync job
app.scheduler.enabled: Enable/disable the scheduler

# Kafka configuration
spring.kafka.bootstrap-servers: Kafka bootstrap servers
```

## API Documentation

API documentation is available at:

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/api-docs

## Testing

The application includes comprehensive testing:

### Unit Tests

Unit tests cover individual components with mocked dependencies:
- `CustomerServiceClientTest`: Tests the client for the external service
- `KafkaPublisherServiceTest`: Tests the Kafka publishing functionality
- `CustomerSyncServiceTest`: Tests the service orchestration
- `CustomerSyncControllerTest`: Tests the REST controller

### Integration Tests

Integration tests verify the interaction between components:
- `CustomerSyncIntegrationTest`: Tests the end-to-end flow with mocked external dependencies

### BDD Tests

Behavior-Driven Development tests written in Gherkin:
- Feature file: `src/test/resources/features/customer_sync.feature`
- Step definitions: `CucumberStepDefinitions.java`
- Test runner: `CucumberTestRunner.java`

To run all tests:

```bash
mvn test
```

To run only unit tests:

```bash
mvn test -Dtest=*Test
```

To run only integration tests:

```bash
mvn test -Dtest=*IntegrationTest
```

To run only BDD tests:

```bash
mvn test -Dtest=CucumberTestRunner
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome. Please feel free to submit a Pull Request. 