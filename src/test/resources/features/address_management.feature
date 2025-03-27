Feature: Address Management
  As a system administrator
  I want to manage addresses (include, query, update and delete)
  To maintain an updated registry of locations in the system

  Scenario: Register a new address successfully
    Given that I have a valid address to register
    When I send a POST request to "/addresses" with the address data
    Then the system should return status 201 (CREATED)
    And return the data of the registered address with the generated ID

  Scenario: Search for address by ID
    Given that there is an address registered with ID "1"
    When I send a GET request to "/addresses/1"
    Then the system should return status 200 (OK)
    And return the address data corresponding

  Scenario: Update existing address
    Given that there is an address registered with ID "1"
    When I send a PUT request to "/addresses/1" with updated data
    Then the system should return status 200 (OK)
    And return the updated address data

  Scenario: Delete existing address
    Given that there is an address registered with ID "1"
    When I send a DELETE request to "/addresses/1"
    Then the system should return status 204 (NO CONTENT)

  Scenario: Search for addresses by State
    Given that there are addresses registered for the State "SP"
    When I send a GET request to "/addresses/state/SP"
    Then the system should return status 200 (OK)
    And return a list with all addresses of this State

  Scenario: List all States
    Given that the system has all States of Brazil registered
    When I send a GET request to "/ufs"
    Then the system should return status 200 (OK)
    And return a list with all 27 States of Brazil 