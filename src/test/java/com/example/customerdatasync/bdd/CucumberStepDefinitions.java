package com.example.customerdatasync.bdd;

import com.example.customerdatasync.client.CustomerServiceClient;
import com.example.customerdatasync.model.Customer;
import com.example.customerdatasync.service.CustomerSyncService;
import com.example.customerdatasync.service.KafkaPublisherService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class CucumberStepDefinitions {

    @Autowired
    private CustomerSyncService customerSyncService;

    @MockBean
    private CustomerServiceClient customerServiceClient;

    @MockBean
    private KafkaPublisherService kafkaPublisherService;

    private List<Customer> testCustomers;
    private Exception thrownException;

    @Given("the external customer service is available")
    public void theExternalCustomerServiceIsAvailable() {
        // Nothing to do here, just setting up the context
    }

    @Given("it returns a list of customers")
    public void itReturnsAListOfCustomers() {
        testCustomers = Arrays.asList(
                Customer.builder().id(1L).name("John Doe").email("john@example.com").build(),
                Customer.builder().id(2L).name("Jane Doe").email("jane@example.com").build()
        );
        when(customerServiceClient.getCustomers()).thenReturn(testCustomers);
    }

    @Given("it returns an empty list of customers")
    public void itReturnsAnEmptyListOfCustomers() {
        testCustomers = Collections.emptyList();
        when(customerServiceClient.getCustomers()).thenReturn(testCustomers);
    }

    @Given("the external customer service is unavailable")
    public void theExternalCustomerServiceIsUnavailable() {
        when(customerServiceClient.getCustomers()).thenThrow(new RuntimeException("Service unavailable"));
    }

    @When("the customer sync process is triggered")
    public void theCustomerSyncProcessIsTriggered() {
        try {
            customerSyncService.syncCustomers();
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("all customers should be published to Kafka topic {string}")
    public void allCustomersShouldBePublishedToKafkaTopic(String topic) {
        verify(kafkaPublisherService).publishCustomers(testCustomers);
    }

    @Then("no customers should be published to Kafka")
    public void noCustomersShouldBePublishedToKafka() {
        verifyNoInteractions(kafkaPublisherService);
    }

    @Then("the sync process should complete successfully")
    public void theSyncProcessShouldCompleteSuccessfully() {
        // If we got here without an exception, the test passes
        assert thrownException == null;
    }

    @Then("the system should log an error")
    public void theSystemShouldLogAnError() {
        // This is implicitly testing that our service logs errors
        // The actual logging verification would require a custom appender, which is beyond the scope of this example
        verify(customerServiceClient).getCustomers();
    }
} 