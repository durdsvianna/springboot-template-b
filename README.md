# Customer Data Sync Service

A microservice that synchronizes customer data from an external customer service to Apache Kafka.

## Features

- **Customer Sync**: Consumes the `listarClientes` method from an external customer microservice at `/api/v1/customer` and publishes to Kafka
- **Address Management**: Complete CRUD operations for managing addresses with validation and BDD testing
- **State Management**: Brazil state management with initial data loading
- Scheduled synchronization of customer data to a Kafka topic
- Manual trigger endpoint for customer data synchronization
- Comprehensive testing suite including unit, integration, and BDD tests

## Technologies

- Java 21
- Spring Boot 3.2.3
- Spring WebFlux (WebClient)
- Spring Data JPA with H2 Database
- Apache Kafka
- Lombok
- JUnit 5
- Cucumber (BDD testing)
- RestAssured
- SpringDoc OpenAPI (Swagger UI)

## Architecture

The application follows a clean architecture approach:

- **Controller Layer**: REST endpoints for manual triggering of the sync process and address management
- **Service Layer**: Business logic for customer data synchronization and address management
- **Repository Layer**: Data access for address and state entities
- **Client Layer**: External service communication using reactive WebClient
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
mvn spring-boot:run
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

# Database configuration
spring.datasource.url: JDBC URL for the database
spring.datasource.username: Database username
spring.datasource.password: Database password
```

## API Documentation

API documentation is available at:

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/api-docs

## Address Management API

### Endpoints

| Method | URL                        | Description                         |
|--------|----------------------------|-------------------------------------|
| GET    | /api/ufs                   | List all states                     |
| POST   | /api/addresses             | Create a new address                |
| GET    | /api/addresses/{id}        | Get address by ID                   |
| PUT    | /api/addresses/{id}        | Update existing address             |
| DELETE | /api/addresses/{id}        | Delete address                      |
| GET    | /api/addresses/state/{code}| Get addresses by state code         |

### Address Model

```json
{
  "id": 1,
  "street": "Avenida Paulista, 1000",
  "complement": "Apt 123",
  "zipCode": "01310-100",
  "city": "São Paulo",
  "state": {
    "stateCode": "SP",
    "name": "São Paulo"
  }
}
```

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
- Feature files:
  - `src/test/resources/features/customer_sync.feature` - For customer sync functionality
  - `src/test/resources/features/address_management.feature` - For address management functionality
- Step definitions:
  - `CucumberStepDefinitions.java` - For customer sync steps
  - `AddressManagementSteps.java` - For address management steps
- Test runner: `AddressManagementTest.java` - JUnit 5 style test runner

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
mvn test -Dtest=AddressManagementTest
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome. Please feel free to submit a Pull Request. 