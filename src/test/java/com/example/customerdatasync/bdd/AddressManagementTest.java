package com.example.customerdatasync.bdd;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/address_management.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.customerdatasync.bdd")
public class AddressManagementTest {
    // This class just serves as an entry point for the Cucumber test runner
} 