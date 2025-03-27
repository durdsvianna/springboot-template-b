package com.example.customerdatasync.bdd;

import com.example.customerdatasync.dto.AddressRequestDto;
import com.example.customerdatasync.dto.AddressResponseDto;
import com.example.customerdatasync.dto.StateDto;
import com.example.customerdatasync.model.Address;
import com.example.customerdatasync.model.State;
import com.example.customerdatasync.repository.AddressRepository;
import com.example.customerdatasync.repository.StateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
public class AddressManagementSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private AddressRepository addressRepository;

    private AddressRequestDto addressRequestDto;
    private ResultActions resultActions;
    private State savedState;
    private Address savedAddress;

    @Before
    public void setup() {
        addressRepository.deleteAll();
        
        // Ensure SP state exists
        if (stateRepository.findById("SP").isEmpty()) {
            savedState = stateRepository.save(new State("SP", "São Paulo"));
        } else {
            savedState = stateRepository.findById("SP").get();
        }
    }

    @Given("that I have a valid address to register")
    public void thatIHaveAValidAddressToRegister() {
        addressRequestDto = new AddressRequestDto(
                "Avenida Paulista, 1000",
                "Apt 123",
                "01310-100",
                "São Paulo",
                "SP"
        );
    }

    @Given("that there is an address registered with ID {string}")
    public void thatThereIsAnAddressRegisteredWithID(String id) {
        Address address = Address.builder()
                .street("Avenida Paulista, 1000")
                .complement("Apt 123")
                .zipCode("01310-100")
                .city("São Paulo")
                .state(savedState)
                .build();
        
        savedAddress = addressRepository.save(address);
    }

    @Given("that there are addresses registered for the State {string}")
    public void thatThereAreAddressesRegisteredForTheState(String stateCode) {
        State state = stateRepository.findById(stateCode)
                .orElseThrow(() -> new IllegalStateException("State not found: " + stateCode));
        
        Address address1 = Address.builder()
                .street("Avenida Paulista, 1000")
                .complement("Apt 123")
                .zipCode("01310-100")
                .city("São Paulo")
                .state(state)
                .build();
        
        Address address2 = Address.builder()
                .street("Rua Augusta, 500")
                .complement("Sala 45")
                .zipCode("01305-000")
                .city("São Paulo")
                .state(state)
                .build();
        
        addressRepository.save(address1);
        addressRepository.save(address2);
    }

    @Given("that the system has all States of Brazil registered")
    public void thatTheSystemHasAllStatesOfBrazilRegistered() {
        // This is handled by the DataLoader in the application
    }

    @When("I send a POST request to {string} with the address data")
    public void iSendAPOSTRequestToWithTheAddressData(String endpoint) throws Exception {
        resultActions = mockMvc.perform(post("/api" + endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestDto)));
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(get("/api" + endpoint));
    }

    @When("I send a PUT request to {string} with updated data")
    public void iSendAPUTRequestToWithUpdatedData(String endpoint) throws Exception {
        AddressRequestDto updatedAddress = new AddressRequestDto(
                "Rua Augusta, 500",
                "Sala 45",
                "01305-000",
                "São Paulo",
                "SP"
        );
        
        resultActions = mockMvc.perform(put("/api" + endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress)));
    }

    @When("I send a DELETE request to {string}")
    public void iSendADELETERequestTo(String endpoint) throws Exception {
        resultActions = mockMvc.perform(delete("/api" + endpoint));
    }

    @Then("the system should return status {int} \\(CREATED)")
    public void theSystemShouldReturnStatusCREATED(int statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Then("the system should return status {int} \\(OK)")
    public void theSystemShouldReturnStatusOK(int statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Then("the system should return status {int} \\(NO CONTENT)")
    public void theSystemShouldReturnStatusNOCONTENT(int statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Then("return the data of the registered address with the generated ID")
    public void returnTheDataOfTheRegisteredAddressWithTheGeneratedID() throws Exception {
        resultActions
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.street").value(addressRequestDto.street()))
                .andExpect(jsonPath("$.complement").value(addressRequestDto.complement()))
                .andExpect(jsonPath("$.zipCode").value(addressRequestDto.zipCode()))
                .andExpect(jsonPath("$.city").value(addressRequestDto.city()))
                .andExpect(jsonPath("$.state.stateCode").value(addressRequestDto.stateCode()));
    }

    @Then("return the address data corresponding")
    public void returnTheAddressDataCorresponding() throws Exception {
        resultActions
                .andExpect(jsonPath("$.id").value(savedAddress.getId()))
                .andExpect(jsonPath("$.street").value(savedAddress.getStreet()))
                .andExpect(jsonPath("$.complement").value(savedAddress.getComplement()))
                .andExpect(jsonPath("$.zipCode").value(savedAddress.getZipCode()))
                .andExpect(jsonPath("$.city").value(savedAddress.getCity()))
                .andExpect(jsonPath("$.state.stateCode").value(savedAddress.getState().getStateCode()));
    }

    @Then("return the updated address data")
    public void returnTheUpdatedAddressData() throws Exception {
        resultActions
                .andExpect(jsonPath("$.id").value(savedAddress.getId()))
                .andExpect(jsonPath("$.street").value("Rua Augusta, 500"))
                .andExpect(jsonPath("$.complement").value("Sala 45"))
                .andExpect(jsonPath("$.zipCode").value("01305-000"))
                .andExpect(jsonPath("$.city").value("São Paulo"))
                .andExpect(jsonPath("$.state.stateCode").value("SP"));
    }

    @Then("return a list with all addresses of this State")
    public void returnAListWithAllAddressesOfThisState() throws Exception {
        MvcResult mvcResult = resultActions
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        List<AddressResponseDto> addresses = objectMapper.readValue(content, new TypeReference<List<AddressResponseDto>>() {});
        
        assertThat(addresses, everyItem(hasProperty("state", hasProperty("stateCode", equalTo("SP")))));
    }

    @Then("return a list with all {int} States of Brazil")
    public void returnAListWithAllStatesOfBrazil(int count) throws Exception {
        MvcResult mvcResult = resultActions
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(count)))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        List<StateDto> states = objectMapper.readValue(content, new TypeReference<List<StateDto>>() {});
        
        // Check a few states to ensure they are in the list
        assertThat(states, hasItem(hasProperty("stateCode", equalTo("SP"))));
        assertThat(states, hasItem(hasProperty("stateCode", equalTo("RJ"))));
        assertThat(states, hasItem(hasProperty("stateCode", equalTo("MG"))));
    }
} 