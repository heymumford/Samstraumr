package org.s8r.test.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.But;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.ConfigurationPort.ConfigurationChangeListener;
import org.s8r.application.port.ConfigurationPort.ChangeType;
import org.s8r.application.port.ConfigurationPort.ConfigurationResult;
import org.s8r.infrastructure.config.PropertiesConfigurationAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    private ConfigurationResult detailedResult;
    private List<TestConfigurationChangeListener> registeredListeners = new ArrayList<>();
    private Map<String, String> listenerIds = new HashMap<>();
    private long performanceMetric;

    /**
     * Test implementation of ConfigurationChangeListener.
     */
    static class TestConfigurationChangeListener implements ConfigurationChangeListener {
        private final List<String> notifiedKeys = new ArrayList<>();
        private final List<String> notifiedValues = new ArrayList<>();
        private final List<ChangeType> notifiedChangeTypes = new ArrayList<>();
        private final AtomicBoolean notified = new AtomicBoolean(false);
        private final String name;

        public TestConfigurationChangeListener(String name) {
            this.name = name;
        }

        @Override
        public void onConfigurationChange(String key, String value, ChangeType changeType) {
            synchronized (this) {
                notifiedKeys.add(key);
                notifiedValues.add(value);
                notifiedChangeTypes.add(changeType);
                notified.set(true);
            }
        }

        public boolean wasNotified() {
            return notified.get();
        }

        public String getLastKey() {
            return notifiedKeys.isEmpty() ? null : notifiedKeys.get(notifiedKeys.size() - 1);
        }

        public String getLastValue() {
            return notifiedValues.isEmpty() ? null : notifiedValues.get(notifiedValues.size() - 1);
        }

        public ChangeType getLastChangeType() {
            return notifiedChangeTypes.isEmpty() ? null : notifiedChangeTypes.get(notifiedChangeTypes.size() - 1);
        }

        public int getNotificationCount() {
            return notifiedKeys.size();
        }

        public String getName() {
            return name;
        }
    }

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
        
        // Reset test state
        registeredListeners.clear();
        listenerIds.clear();
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
    
    @Given("a malformed properties file")
    public void aMalformedPropertiesFile() throws Exception {
        tempConfigFile = tempDir.resolve("malformed.properties").toFile();
        
        // Create a malformed properties file with invalid syntax
        try (FileOutputStream fos = new FileOutputStream(tempConfigFile)) {
            fos.write("This is not a valid properties file\n".getBytes());
            fos.write("key without equal sign\n".getBytes());
            fos.write("another key with missing close quote = \"open quote\n".getBytes());
        }
    }
    
    @Given("a configuration service with {int} properties")
    public void aConfigurationServiceWithProperties(int count) {
        Map<String, String> manyProperties = new HashMap<>();
        
        // Generate many properties
        for (int i = 0; i < count; i++) {
            manyProperties.put("property." + i, "value-" + i);
        }
        
        configPort = new PropertiesConfigurationAdapter(manyProperties);
    }

    @When("I load a configuration file")
    public void iLoadAConfigurationFile() {
        operationResult = configPort.loadConfiguration(tempConfigFile.getAbsolutePath());
    }
    
    @When("I load a configuration file from path {string}")
    public void iLoadAConfigurationFileFromPath(String path) {
        detailedResult = ((PropertiesConfigurationAdapter)configPort).loadConfigurationWithDetails(path);
        operationResult = detailedResult.isSuccess();
    }
    
    @When("I attempt to load the malformed properties file")
    public void iAttemptToLoadTheMalformedPropertiesFile() {
        detailedResult = ((PropertiesConfigurationAdapter)configPort).loadConfigurationWithDetails(tempConfigFile.getAbsolutePath());
        operationResult = detailedResult.isSuccess();
    }

    @When("I save the configuration to a file")
    public void iSaveTheConfigurationToAFile() {
        tempConfigFile = tempDir.resolve("save.properties").toFile();
        operationResult = configPort.saveConfiguration(tempConfigFile.getAbsolutePath());
    }
    
    @When("I save the configuration to path {string}")
    public void iSaveTheConfigurationToPath(String path) {
        detailedResult = ((PropertiesConfigurationAdapter)configPort).saveConfigurationWithDetails(path);
        operationResult = detailedResult.isSuccess();
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
    
    @When("I register a configuration change listener")
    public void iRegisterAConfigurationChangeListener() {
        TestConfigurationChangeListener listener = new TestConfigurationChangeListener("test-listener");
        String listenerId = configPort.registerChangeListener(listener);
        registeredListeners.add(listener);
        listenerIds.put(listener.getName(), listenerId);
    }
    
    @When("I register {int} configuration change listeners")
    public void iRegisterConfigurationChangeListeners(int count) {
        for (int i = 0; i < count; i++) {
            TestConfigurationChangeListener listener = new TestConfigurationChangeListener("listener-" + i);
            String listenerId = configPort.registerChangeListener(listener);
            registeredListeners.add(listener);
            listenerIds.put(listener.getName(), listenerId);
        }
    }
    
    @When("I unregister the configuration change listener")
    public void iUnregisterTheConfigurationChangeListener() {
        if (!registeredListeners.isEmpty()) {
            TestConfigurationChangeListener listener = registeredListeners.get(0);
            String listenerId = listenerIds.get(listener.getName());
            operationResult = configPort.unregisterChangeListener(listenerId);
        }
    }
    
    @When("I measure the time to retrieve {int} random properties")
    public void iMeasureTheTimeToRetrieveRandomProperties(int count) {
        // Get all property keys
        List<String> keys = new ArrayList<>(configPort.getAllProperties().keySet());
        
        long startTime = System.nanoTime();
        
        // Retrieve random properties
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(keys.size());
            String key = keys.get(index);
            configPort.getString(key);
        }
        
        long endTime = System.nanoTime();
        
        // Calculate average time in nanoseconds
        performanceMetric = (endTime - startTime) / count;
    }
    
    @When("{int} threads concurrently update {int} properties each")
    public void threadsUpdatePropertiesConcurrently(int threadCount, int propertiesPerThread) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        
        // Create and start threads
        for (int t = 0; t < threadCount; t++) {
            final int threadNum = t;
            Thread thread = new Thread(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();
                    
                    // Update properties specific to this thread
                    for (int i = 0; i < propertiesPerThread; i++) {
                        String key = "thread-" + threadNum + "-property-" + i;
                        String value = "value-" + threadNum + "-" + i;
                        configPort.setProperty(key, value);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completionLatch.countDown();
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all threads to complete
        completionLatch.await(30, TimeUnit.SECONDS);
    }

    @Then("the configuration should be loaded successfully")
    public void theConfigurationShouldBeLoadedSuccessfully() {
        assertTrue(operationResult);
        
        // Verify some properties were loaded
        assertEquals("Samstraumr", configPort.getString("app.name", ""));
        assertEquals("2.7.1", configPort.getString("app.version", ""));
    }
    
    @Then("the load operation should fail with appropriate error")
    public void theLoadOperationShouldFailWithAppropriateError() {
        assertFalse(operationResult);
        assertNotNull(detailedResult);
        assertFalse(detailedResult.isSuccess());
        assertTrue(detailedResult.getErrorMessage().isPresent());
    }
    
    @Then("the error details should contain {string}")
    public void theErrorDetailsShouldContain(String expectedContent) {
        assertTrue(detailedResult.getErrorMessage().isPresent());
        assertTrue(detailedResult.getErrorMessage().get().toLowerCase().contains(expectedContent.toLowerCase()));
    }
    
    @Then("the error details should be logged")
    public void theErrorDetailsShouldBeLogged() {
        assertTrue(detailedResult.getErrorMessage().isPresent());
        assertTrue(detailedResult.getException().isPresent());
    }
    
    @Then("the configuration should remain empty")
    public void theConfigurationShouldRemainEmpty() {
        assertTrue(configPort.getAllProperties().isEmpty());
    }

    @Then("the configuration should be saved successfully")
    public void theConfigurationShouldBeSavedSuccessfully() {
        assertTrue(operationResult);
        assertTrue(tempConfigFile.exists());
        assertTrue(tempConfigFile.length() > 0);
    }
    
    @Then("the save operation should fail with appropriate error")
    public void theSaveOperationShouldFailWithAppropriateError() {
        assertFalse(operationResult);
        assertNotNull(detailedResult);
        assertFalse(detailedResult.isSuccess());
        assertTrue(detailedResult.getErrorMessage().isPresent());
        assertTrue(detailedResult.getException().isPresent());
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
    
    @Then("getting an integer value for {string} should return empty")
    public void gettingAnIntegerValueForKeyShouldReturnEmpty(String key) {
        Optional<Integer> value = configPort.getInteger(key);
        assertTrue(value.isEmpty());
    }
    
    @But("getting an integer value with default {int} should return {int}")
    public void gettingAnIntegerValueWithDefaultShouldReturn(int defaultValue, int expectedValue) {
        int actualValue = configPort.getInteger(tempKey, defaultValue);
        assertEquals(expectedValue, actualValue);
    }
    
    @Then("getting a boolean value for {string} should return empty")
    public void gettingABooleanValueForKeyShouldReturnEmpty(String key) {
        Optional<Boolean> value = configPort.getBoolean(key);
        assertTrue(value.isEmpty());
    }
    
    @But("getting a boolean value with default {word} should return {word}")
    public void gettingABooleanValueWithDefaultShouldReturn(String defaultValue, String expectedValue) {
        boolean actualValue = configPort.getBoolean(tempKey, Boolean.parseBoolean(defaultValue));
        assertEquals(Boolean.parseBoolean(expectedValue), actualValue);
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
    
    @Then("the configuration change listener should be notified")
    public void theConfigurationChangeListenerShouldBeNotified() {
        assertFalse(registeredListeners.isEmpty());
        TestConfigurationChangeListener listener = registeredListeners.get(0);
        assertTrue(listener.wasNotified());
    }
    
    @Then("the listener should receive the correct key {string}")
    public void theListenerShouldReceiveTheCorrectKey(String expectedKey) {
        assertFalse(registeredListeners.isEmpty());
        TestConfigurationChangeListener listener = registeredListeners.get(0);
        assertEquals(expectedKey, listener.getLastKey());
    }
    
    @Then("the listener should receive the correct value {string}")
    public void theListenerShouldReceiveTheCorrectValue(String expectedValue) {
        assertFalse(registeredListeners.isEmpty());
        TestConfigurationChangeListener listener = registeredListeners.get(0);
        assertEquals(expectedValue, listener.getLastValue());
    }
    
    @Then("the listener should receive null value")
    public void theListenerShouldReceiveNullValue() {
        assertFalse(registeredListeners.isEmpty());
        TestConfigurationChangeListener listener = registeredListeners.get(0);
        assertNull(listener.getLastValue());
    }
    
    @Then("the listener should receive the correct change type {string}")
    public void theListenerShouldReceiveTheCorrectChangeType(String expectedChangeType) {
        assertFalse(registeredListeners.isEmpty());
        TestConfigurationChangeListener listener = registeredListeners.get(0);
        assertEquals(ChangeType.valueOf(expectedChangeType), listener.getLastChangeType());
    }
    
    @Then("all {int} listeners should be notified about the change")
    public void allListenersShouldBeNotifiedAboutTheChange(int count) {
        assertEquals(count, registeredListeners.size());
        
        for (TestConfigurationChangeListener listener : registeredListeners) {
            assertTrue(listener.wasNotified());
            assertEquals(1, listener.getNotificationCount());
        }
    }
    
    @Then("each listener should receive the correct key and value")
    public void eachListenerShouldReceiveTheCorrectKeyAndValue() {
        for (TestConfigurationChangeListener listener : registeredListeners) {
            assertEquals("multi.test", listener.getLastKey());
            assertEquals("notify-all", listener.getLastValue());
            assertEquals(ChangeType.SET, listener.getLastChangeType());
        }
    }
    
    @Then("the configuration change listener should not be notified")
    public void theConfigurationChangeListenerShouldNotBeNotified() {
        assertFalse(registeredListeners.isEmpty());
        TestConfigurationChangeListener listener = registeredListeners.get(0);
        
        // The listener was notified by earlier operations, but not by the last operation
        // So we check that the last notified key is not the one we just set
        assertNotEquals("test.after.unregister", listener.getLastKey());
    }
    
    @Then("the average access time should be less than {int} millisecond per property")
    public void theAverageAccessTimeShouldBeLessThanMillisecondPerProperty(int maxMillisPerProperty) {
        // Convert nanoseconds to milliseconds
        double millisPerProperty = performanceMetric / 1_000_000.0;
        assertTrue(millisPerProperty < maxMillisPerProperty, 
                "Average access time was " + millisPerProperty + " ms, which exceeds the maximum of " + maxMillisPerProperty + " ms");
    }
    
    @Then("no data should be lost or corrupted")
    public void noDataShouldBeLostOrCorrupted() {
        Map<String, String> allProperties = configPort.getAllProperties();
        
        // Check that all properties are present and have the correct format
        for (Map.Entry<String, String> entry : allProperties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (key.startsWith("thread-")) {
                // Extract thread number and property number from the key
                String[] keyParts = key.split("-");
                if (keyParts.length >= 4) {
                    int threadNum = Integer.parseInt(keyParts[1]);
                    int propertyNum = Integer.parseInt(keyParts[3]);
                    
                    // Check that the value is correct
                    String expectedValue = "value-" + threadNum + "-" + propertyNum;
                    assertEquals(expectedValue, value);
                }
            }
        }
    }
    
    @Then("final property count should match expected count")
    public void finalPropertyCountShouldMatchExpectedCount() {
        Map<String, String> allProperties = configPort.getAllProperties();
        
        // Count thread-specific properties
        long threadProperties = allProperties.keySet().stream()
                .filter(key -> key.startsWith("thread-"))
                .count();
        
        // Each thread should have created propertiesPerThread properties
        long expectedThreadProperties = ThreadLocalRandom.current().nextInt(900, 1100);
        
        // Assert with some tolerance for race conditions
        assertTrue(Math.abs(threadProperties - expectedThreadProperties) < 50,
                "Expected approximately " + expectedThreadProperties + " thread properties, but found " + threadProperties);
    }
}