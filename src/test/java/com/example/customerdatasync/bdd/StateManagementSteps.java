package com.example.customerdatasync.bdd;

import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.exception.ResourceNotFoundException;
import com.example.customerdatasync.exception.ServiceException;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.service.StateService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StateManagementSteps {
    
    private StateService stateService;
    
    private List<StateDto> stateResponses;
    private State stateResponse;
    private StateDto stateDetailsResponse;
    private Exception thrownException;
    private State state;
    
    @Before
    public void setup() {
        stateService = mock(StateService.class);
        thrownException = null;
    }
    
    @Given("the following states exist in the system:")
    public void theFollowingStatesExistInTheSystem(DataTable dataTable) {
        List<Map<String, String>> states = dataTable.asMaps();
        List<StateDto> stateList = states.stream()
                .map(state -> new StateDto(
                        state.get("stateCode"),
                        state.get("name")
                ))
                .collect(Collectors.toList());
        
        when(stateService.getAllStates()).thenReturn(stateList);
        
        states.forEach(state -> {
            State modelState = new State(state.get("stateCode"), state.get("name"));
            when(stateService.getStateByCode(state.get("stateCode")))
                    .thenReturn(modelState);
            when(stateService.getStateDetails(state.get("stateCode")))
                    .thenReturn(new StateDto(state.get("stateCode"), state.get("name")));
        });
        
        // Configure behavior for non-existent state codes
        when(stateService.getStateByCode("XX"))
            .thenThrow(new ResourceNotFoundException("State not found"));
    }
    
    @Given("no states exist in the system")
    public void noStatesExistInTheSystem() {
        when(stateService.getAllStates()).thenReturn(List.of());
    }
    
    @Given("the database is experiencing issues for states")
    public void theDatabaseIsExperiencingIssuesForStates() {
        when(stateService.getAllStates())
                .thenThrow(new ServiceException("Failed to retrieve all states"));
        when(stateService.getStateByCode(anyString()))
                .thenThrow(new DataAccessException("Database error") {});
    }
    
    @When("I request all states")
    public void iRequestAllStates() {
        try {
            stateResponses = stateService.getAllStates();
        } catch (Exception e) {
            thrownException = e;
        }
    }
    
    @When("I request state with code {string}")
    public void iRequestStateWithCode(String stateCode) {
        try {
            state = stateService.getStateByCode(stateCode);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            state = null;
            System.out.println("Exception caught: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }
    
    @When("I request state details for code {string}")
    public void iRequestStateDetailsForCode(String stateCode) {
        try {
            stateDetailsResponse = stateService.getStateDetails(stateCode);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    
    @When("I try to retrieve all states")
    public void iTryToRetrieveAllStates() {
        try {
            stateResponses = stateService.getAllStates();
        } catch (Exception e) {
            thrownException = e;
        }
    }
    
    @Then("I should receive {int} states")
    public void iShouldReceiveStates(int count) {
        assertNotNull(stateResponses);
        assertEquals(count, stateResponses.size());
    }
    
    @Then("the states should include:")
    public void theStatesShouldInclude(DataTable dataTable) {
        List<Map<String, String>> expectedStates = dataTable.asMaps();
        assertEquals(expectedStates.size(), stateResponses.size());
        
        for (int i = 0; i < expectedStates.size(); i++) {
            Map<String, String> expected = expectedStates.get(i);
            StateDto actual = stateResponses.get(i);
            
            assertEquals(expected.get("stateCode"), actual.stateCode());
            assertEquals(expected.get("name"), actual.name());
        }
    }
    
    @Then("I should receive the state successfully")
    public void iShouldReceiveTheStateSuccessfully() {
        assertNull(thrownException);
        assertNotNull(state);
    }
    
    @Then("the state should have name {string}")
    public void theStateShouldHaveName(String name) {
        assertEquals(name, state.getName());
    }
    
    @Then("I should receive the state details successfully")
    public void iShouldReceiveTheStateDetailsSuccessfully() {
        assertNull(thrownException);
        assertNotNull(stateDetailsResponse);
    }
    
    @Then("the state details should include:")
    public void theStateDetailsShouldInclude(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMaps().get(0);
        assertEquals(expected.get("stateCode"), stateDetailsResponse.stateCode());
        assertEquals(expected.get("name"), stateDetailsResponse.name());
    }
    
    @Then("I should receive a resource not found error for state")
    public void iShouldReceiveAResourceNotFoundErrorForState() {
        assertThat(thrownException).isInstanceOf(ResourceNotFoundException.class);
    }
    
    @Then("I should receive a service error for state")
    public void iShouldReceiveAServiceErrorForState() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof ServiceException);
    }
    
    @Then("the state error message should contain {string}")
    public void theStateErrorMessageShouldContain(String errorMessage) {
        assertThat(thrownException.getMessage()).contains(errorMessage);
    }
    
    @Then("I should receive an empty list of states")
    public void iShouldReceiveAnEmptyListOfStates() {
        assertNotNull(stateResponses);
        assertTrue(stateResponses.isEmpty());
    }
    
    @Given("a state with code {string} does not exist")
    public void aStateWithCodeDoesNotExist(String stateCode) {
        when(stateService.getStateByCode(stateCode))
            .thenThrow(new ResourceNotFoundException("State not found with code: " + stateCode));
    }
} 