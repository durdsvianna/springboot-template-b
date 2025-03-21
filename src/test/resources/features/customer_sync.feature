Feature: Customer Synchronization
  As a system administrator
  I want to synchronize customer data from another service to Kafka
  So that other systems can consume up-to-date customer information

  Scenario: Successfully sync customers from the external service to Kafka
    Given the external customer service is available
    And it returns a list of customers
    When the customer sync process is triggered
    Then all customers should be published to Kafka topic "CLIENTES_HOJE"
    And the sync process should complete successfully

  Scenario: Handle empty customer list from external service
    Given the external customer service is available
    But it returns an empty list of customers
    When the customer sync process is triggered
    Then no customers should be published to Kafka
    And the sync process should complete successfully

  Scenario: Handle external service unavailability
    Given the external customer service is unavailable
    When the customer sync process is triggered
    Then the system should log an error
    And no customers should be published to Kafka 