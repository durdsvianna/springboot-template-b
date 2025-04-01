Feature: State Management
  As a user
  I want to manage states
  So that I can associate addresses with the correct state

  Scenario: Get all states
    Given the following states exist in the system:
      | stateCode | name       |
      | SP        | São Paulo  |
      | RJ        | Rio de Janeiro |
      | MG        | Minas Gerais |
    When I request all states
    Then I should receive 3 states
    And the states should include:
      | stateCode | name       |
      | SP        | São Paulo  |
      | RJ        | Rio de Janeiro |
      | MG        | Minas Gerais |

  Scenario: Get state by code
    Given the following states exist in the system:
      | stateCode | name       |
      | SP        | São Paulo  |
    When I request state with code "SP"
    Then I should receive the state successfully
    And the state should have name "São Paulo"

  Scenario: Get state details
    Given the following states exist in the system:
      | stateCode | name       |
      | SP        | São Paulo  |
    When I request state details for code "SP"
    Then I should receive the state details successfully
    And the state details should include:
      | stateCode | name       |
      | SP        | São Paulo  |

  Scenario: Get state by non-existent code
    Given a state with code "XX" does not exist
    When I request state with code "XX"
    Then I should receive a resource not found error for state
    And the state error message should contain "State not found"

  Scenario: No states exist
    Given no states exist in the system
    When I request all states
    Then I should receive an empty list of states

  Scenario: Database error retrieving states
    Given the database is experiencing issues for states
    When I try to retrieve all states
    Then I should receive a service error for state
    And the state error message should contain "Failed to retrieve all states" 