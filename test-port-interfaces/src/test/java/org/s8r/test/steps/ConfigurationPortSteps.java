package org.s8r.test.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.infrastructure.config.PropertiesConfigurationAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationPortSteps {

    private ConfigurationPort configPort;
    private Path tempDir;
    private File tempConfigFile;
    private String tempKey;
    private Object tempValue;
    private Map<String, String> initialProperties;
    private List<String> stringList;
    private Map<String, String> filteredProperties;
    private boolean operationResult;

    @Before
    public void setUp() throws Exception {
        // Create temporary directory for test files
        tempDir = Files.createTempDirectory("config-test");
        
        // Initialize test properties
        initialProperties = new HashMap<>();
        initialProperties.put("app.name", "Samstraumr");
        initialProperties.put("app.version", "2.7.1");
        initialProperties.put("app.debug", "true");
        initialProperties.put("app.maxConnections", "100");
        initialProperties.put("app.tags", "java,clean-architecture,testing");
        initialProperties.put("module.cache.enabled", "true");
        initialProperties.put("module.cache.size", "1024");
        initialProperties.put("module.auth.enabled", "false");
    }

    @After
    public void tearDown() throws Exception {
        // Delete temporary files
        if (tempConfigFile != null && tempConfigFile.exists()) {
            tempConfigFile.delete();
        }
        
        // Delete temporary directory
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @Given("a configuration service")
    public void aConfigurationService() {
        configPort = new PropertiesConfigurationAdapter();
    }

    @Given("a configuration service with default properties")
    public void aConfigurationServiceWithDefaultProperties() {
        configPort = new PropertiesConfigurationAdapter(initialProperties);
    }

    @Given("a properties file with test data")
    public void aPropertiesFileWithTestData() throws Exception {
        tempConfigFile = tempDir.resolve("test.properties").toFile();
        Properties props = new Properties();
        props.putAll(initialProperties);
        
        try (FileOutputStream fos = new FileOutputStream(tempConfigFile)) {
            props.store(fos, "Test properties for ConfigurationPort");
        }
    }

    @When("I load a configuration file")
    public void iLoadAConfigurationFile() {
        operationResult = configPort.loadConfiguration(tempConfigFile.getAbsolutePath());
    }

    @When("I save the configuration to a file")
    public void iSaveTheConfigurationToAFile() {
        tempConfigFile = tempDir.resolve("save.properties").toFile();
        operationResult = configPort.saveConfiguration(tempConfigFile.getAbsolutePath());
    }

    @When("I set a string property {string} to {string}")
    public void iSetAStringPropertyTo(String key, String value) {
        tempKey = key;
        tempValue = value;
        configPort.setProperty(key, value);
    }

    @When("I set an integer property {string} to {int}")
    public void iSetAnIntegerPropertyTo(String key, int value) {
        tempKey = key;
        tempValue = value;
        configPort.setProperty(key, String.valueOf(value));
    }

    @When("I set a boolean property {string} to {string}")
    public void iSetABooleanPropertyTo(String key, String value) {
        tempKey = key;
        tempValue = Boolean.parseBoolean(value);
        configPort.setProperty(key, value);
    }

    @When("I remove the property {string}")
    public void iRemoveTheProperty(String key) {
        tempKey = key;
        operationResult = configPort.removeProperty(key);
    }

    @When("I check if property {string} exists")
    public void iCheckIfPropertyExists(String key) {
        tempKey = key;
        operationResult = configPort.hasProperty(key);
    }

    @When("I get all properties")
    public void iGetAllProperties() {
        filteredProperties = configPort.getAllProperties();
    }

    @When("I get properties with prefix {string}")
    public void iGetPropertiesWithPrefix(String prefix) {
        filteredProperties = configPort.getPropertiesWithPrefix(prefix);
    }

    @When("I get a string list for key {string}")
    public void iGetAStringListForKey(String key) {
        tempKey = key;
        stringList = configPort.getStringList(key);
    }

    @When("I clear all configuration properties")
    public void iClearAllConfigurationProperties() {
        configPort.clear();
    }

    @Then("the configuration should be loaded successfully")
    public void theConfigurationShouldBeLoadedSuccessfully() {
        assertTrue(operationResult);
        
        // Verify some properties were loaded
        assertEquals("Samstraumr", configPort.getString("app.name", ""));
        assertEquals("2.7.1", configPort.getString("app.version", ""));
    }

    @Then("the configuration should be saved successfully")
    public void theConfigurationShouldBeSavedSuccessfully() {
        assertTrue(operationResult);
        assertTrue(tempConfigFile.exists());
        assertTrue(tempConfigFile.length() > 0);
    }

    @Then("I should get string value {string} for key {string}")
    public void iShouldGetStringValueForKey(String expectedValue, String key) {
        assertEquals(expectedValue, configPort.getString(key, ""));
    }

    @Then("I should get string value {string} for key {string} with default {string}")
    public void iShouldGetStringValueForKeyWithDefault(String expectedValue, String key, String defaultValue) {
        assertEquals(expectedValue, configPort.getString(key, defaultValue));
    }

    @Then("I should get integer value {int} for key {string}")
    public void iShouldGetIntegerValueForKey(int expectedValue, String key) {
        Optional<Integer> value = configPort.getInteger(key);
        assertTrue(value.isPresent());
        assertEquals(expectedValue, value.get());
    }

    @Then("I should get integer value {int} for key {string} with default {int}")
    public void iShouldGetIntegerValueForKeyWithDefault(int expectedValue, String key, int defaultValue) {
        assertEquals(expectedValue, configPort.getInteger(key, defaultValue));
    }

    @Then("I should get boolean value {string} for key {string}")
    public void iShouldGetBooleanValueForKey(String expectedValue, String key) {
        Optional<Boolean> value = configPort.getBoolean(key);
        assertTrue(value.isPresent());
        assertEquals(Boolean.parseBoolean(expectedValue), value.get());
    }

    @Then("I should get boolean value {string} for key {string} with default {string}")
    public void iShouldGetBooleanValueForKeyWithDefault(String expectedValue, String key, String defaultValue) {
        assertEquals(Boolean.parseBoolean(expectedValue), 
                     configPort.getBoolean(key, Boolean.parseBoolean(defaultValue)));
    }

    @Then("the string list should contain values {string}")
    public void theStringListShouldContainValues(String expectedValuesCSV) {
        List<String> expectedValues = Arrays.asList(expectedValuesCSV.split(","));
        assertNotNull(stringList);
        assertEquals(expectedValues.size(), stringList.size());
        for (String value : expectedValues) {
            assertTrue(stringList.contains(value.trim()), 
                       "List should contain value: " + value.trim());
        }
    }

    @Then("the property {string} should exist")
    public void thePropertyShouldExist(String key) {
        assertTrue(configPort.hasProperty(key));
    }

    @Then("the property {string} should not exist")
    public void thePropertyShouldNotExist(String key) {
        assertFalse(configPort.hasProperty(key));
    }

    @Then("the operation result should be {string}")
    public void theOperationResultShouldBe(String expected) {
        assertEquals(Boolean.parseBoolean(expected), operationResult);
    }

    @Then("I should get {int} properties")
    public void iShouldGetProperties(int count) {
        assertEquals(count, filteredProperties.size());
    }

    @Then("the filtered properties should contain key {string}")
    public void theFilteredPropertiesShouldContainKey(String key) {
        assertTrue(filteredProperties.containsKey(key));
    }

    @Then("the filtered properties should not contain key {string}")
    public void theFilteredPropertiesShouldNotContainKey(String key) {
        assertFalse(filteredProperties.containsKey(key));
    }

    @Then("the configuration should be empty")
    public void theConfigurationShouldBeEmpty() {
        assertTrue(configPort.getAllProperties().isEmpty());
    }
}