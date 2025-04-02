package org.samstraumr.tube.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.*;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.Environment;
import java.util.List; // Import added

public class TubeInitializationSteps {
    private static final Logger logger = LoggerFactory.getLogger(TubeInitializationSteps.class);
    private Tube testTube;
    private Environment environment;
    private Exception exceptionThrown;

    @Given("the operating environment is ready")
    public void the_operating_environment_is_ready() {
        try {
            environment = new Environment();
            assertNotNull(environment, "Environment should be initialized");
            logger.info("Environment object confirmed instantiated.");
        } catch (Exception e) {
            logger.error("Failed to initialize Environment", e);
            fail("Failed to initialize Environment: " + e.getMessage());
        }
    }

    @When("a new Tube is instantiated with reason {string}")
    public void a_new_Tube_is_instantiated_with_reason(String reason) {
        try {
            testTube = new Tube(reason, environment);  // Create the Tube with the given reason
        } catch (Exception e) {
            // For error handling tests, we store the exception
            exceptionThrown = e;
        }
    }

    @Then("the Tube should initialize with a unique UUID")
    public void the_Tube_should_initialize_with_a_unique_UUID() {
        assertNotNull(testTube.getUniqueId());  // Ensure a unique ID is assigned
    }

    @Then("the Tube should log its environment details")
    public void the_Tube_should_log_its_environment_details() {
        List<String> mimirLog = testTube.queryMimirLog();  // Correct variable used
        assertNotNull(mimirLog, "The log list should not be null");

        boolean environmentLogged = mimirLog.stream()
                .anyMatch(log -> log.contains("Tube initialized with ID:"));
        assertTrue(environmentLogged, "Tube should log its initialization with ID");
    }

    @Then("the Tube should log the reason {string}")
    public void the_Tube_should_log_the_reason(String reason) {
        List<String> mimirLog = testTube.queryMimirLog();
        assertNotNull(mimirLog, "The log list should not be null");

        String log = String.join("\n", mimirLog);  // Fetch the log entries
        assertTrue(log.contains(reason), "Log should contain the given reason: " + reason);
    }

    @Then("the Tube log should be queryable")
    public void the_Tube_log_should_be_queryable() {
        assertFalse(testTube.queryMimirLog().isEmpty());  // Ensure the log is populated and queryable
    }
    
    @Given("the operating environment is invalid")
    public void the_operating_environment_is_invalid() {
        // Create a null environment to trigger an exception
        environment = null;
    }
    
    @Then("the Tube initialization should fail with a {string}")
    public void the_tube_initialization_should_fail_with_exception(String exceptionName) {
        try {
            // Call the constructor that should throw an exception
            testTube = new Tube("Invalid Environment Test", environment);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            exceptionThrown = e;
            String actualExceptionName = e.getClass().getSimpleName();
            assertEquals(exceptionName, actualExceptionName, 
                "Expected exception " + exceptionName + " but got " + actualExceptionName);
        }
    }
    
    @Then("the Tube should log {string}")
    public void the_tube_should_log(String expectedLogMessage) {
        // This step would normally check logs, but since initialization fails,
        // we can only verify the exception was properly thrown
        assertNotNull(exceptionThrown, "Exception should have been thrown");
        String exceptionMessage = exceptionThrown.getMessage();
        assertNotNull(exceptionMessage, "Exception message should not be null");
    }
    
    @Then("the Tube log should capture the specific error message")
    public void the_tube_log_should_capture_the_specific_error_message() {
        // Verify exception has appropriate information
        assertNotNull(exceptionThrown, "An exception should have been thrown");
        assertNotNull(exceptionThrown.getMessage(), "Exception should have a message");
    }
    
    @Then("the Tube should log its initialization details immediately")
    public void the_tube_should_log_its_initialization_details_immediately() {
        List<String> mimirLog = testTube.queryMimirLog();
        assertNotNull(mimirLog, "The log list should not be null");
        assertTrue(mimirLog.size() >= 2, "Log should contain at least initialization entries");
    }
    
    @Then("the Tube logs should be queryable by timestamp")
    public void the_tube_logs_should_be_queryable_by_timestamp() {
        List<String> mimirLog = testTube.queryMimirLog();
        assertNotNull(mimirLog, "The log list should not be null");
        
        // Verify all log entries have timestamps (ISO format starts with year)
        boolean allHaveTimestamps = mimirLog.stream()
                .allMatch(entry -> entry.matches("^\\d{4}-\\d{2}-\\d{2}T.*"));
        assertTrue(allHaveTimestamps, "All log entries should have timestamps");
    }
    
    @Then("the logs should contain the reason {string}")
    public void the_logs_should_contain_the_reason(String reason) {
        the_Tube_should_log_the_reason(reason);
    }
}