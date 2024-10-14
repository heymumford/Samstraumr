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
        testTube = new Tube(reason, environment);  // Create the Tube with the given reason
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
}