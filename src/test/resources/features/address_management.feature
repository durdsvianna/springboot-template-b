Feature: Address Management
  As a user
  I want to manage addresses
  So that I can store and retrieve location information

  Background:
    Given the system has a state "SP" with name "São Paulo"

  Scenario: Successfully create a new address
    When I create an address with the following details:
      | street        | complement | zipCode  | city        | stateCode |
      | Main Street   | Apt 101    | 12345678 | Springfield | SP        |
    Then the address should be created successfully
    And the address should have a formatted zip code "12345-678"
    And the address should be associated with state "SP"

  Scenario: Create address with non-existent state
    When I create an address with state code "XX"
    Then I should receive a resource not found error for address
    And the address error message should contain "State not found"

  Scenario: Update an existing address
    Given an address exists with ID "1"
    When I update the address with the following details:
      | street        | complement | zipCode  | city        | stateCode |
      | New Street    | Apt 202    | 87654321 | Newtown     | SP        |
    Then the address should be updated successfully
    And the address should have the new street "New Street"

  Scenario: Delete an existing address
    Given an address exists with ID "1"
    When I delete the address
    Then the address should be deleted successfully

  Scenario: Attempt to delete non-existent address
    When I try to delete an address with ID "999"
    Then I should receive a resource not found error for address
    And the address error message should contain "Address not found"

  Scenario: Get addresses by state
    Given the following addresses exist for state "SP":
      | id | street      | complement | zipCode    | city      |
      | 1  | Street One  | Apt 101    | 12345-678  | City One  |
      | 2  | Street Two  | Apt 202    | 23456-789  | City Two  |
    When I request all addresses for state "SP"
    Then I should receive 2 addresses
    And all addresses should be in state "SP"

  Scenario: Get addresses for state with no addresses
    When I request all addresses for state "SP"
    Then I should receive an empty list of addresses

  Scenario: Database error while saving address
    Given the database is experiencing issues for addresses
    When I try to create a new address
    Then I should receive a service error for address
    And the address error message should contain "Failed to create address" 