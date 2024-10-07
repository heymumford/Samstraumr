package org.samstraumr.tube.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.*;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.Environment;

public class TubeInitializationSteps {
    private static final Logger logger = LoggerFactory.getLogger(TubeInitializationSteps.class);

    private Tube testTube;
    private Environment environment;


    @Given("the operating environment is ready")
    public void the_operating_environment_is_ready() {
        try {
            environment = new Environment();
            assertNotNull(environment, "Environment should be initialized");
            logger.info("Operating environment is ready");
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
        String log = String.join("\n", testTube.queryMimirLog());  // Fetch the log entries
        assertTrue(log.contains("environment"));
    }

    @Then("the Tube should log the reason {string}")
    public void the_Tube_should_log_the_reason(String reason) {
        String log = String.join("\n", testTube.queryMimirLog());  // Fetch the log entries
        assertTrue(log.contains(reason));  // Ensure the reason is logged
    }

    @Then("the Tube log should be queryable")
    public void the_Tube_log_should_be_queryable() {
        assertFalse(testTube.queryMimirLog().isEmpty());  // Ensure the log is populated and queryable
    }
}
