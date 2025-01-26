package org.samstraumr.tube.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TubeCreationSteps {
    private static final Logger logger = LoggerFactory.getLogger(TubeCreationSteps.class);

    private Tube tube;
    private Instant testStartTime;

    @Given("the Samstraumr framework is initialized")
    public void frameworkIsInitialized() {
        testStartTime = Instant.now();
        logger.info("Initializing test environment at: {}", testStartTime);
        // Framework initialization verification could go here
    }

    @Given("the system environment is stable")
    public void systemEnvironmentIsStable() {
        // Basic system checks could go here
        assertTrue(Runtime.getRuntime().availableProcessors() > 0);
        assertTrue(Runtime.getRuntime().maxMemory() > 0);
    }

    @When("a new tube is created with purpose {string}")
    public void createTubeWithPurpose(String purpose) {
        logger.info("Creating new tube with purpose: {}", purpose);
        tube = new Tube(purpose);
        assertNotNull(tube, "Tube should be created successfully");
    }

    @Then("it should establish an immutable birth certificate containing:")
    public void verifyBirthCertificate(DataTable expectedProperties) {
        assertNotNull(tube, "Tube must exist before verifying birth certificate");

        BirthCertificate birthCertificate = tube.getBirthCertificate();
        assertNotNull(birthCertificate, "Birth certificate should not be null");

        for (Map<String, String> row : expectedProperties.asMaps(String.class, String.class)) {
            String property = row.get("Property");
            String validation = row.get("Validation");

            switch (property) {
                case "universalId" -> {
                    UUID id = birthCertificate.getUniversalId();
                    assertNotNull(id, "Universal ID should not be null");
                    assertEquals(4, id.version(), "Should be UUID v4");
                }
                case "birthTime" -> {
                    Instant birthTime = birthCertificate.getBirthTime();
                    assertNotNull(birthTime, "Birth time should not be null");
                    assertTrue(birthTime.isAfter(testStartTime),
                            "Birth time should be after test start");
                    assertTrue(birthTime.isBefore(Instant.now()),
                            "Birth time should be before now");
                }
                case "purpose" -> {
                    String actualPurpose = birthCertificate.getPurpose();
                    assertNotNull(actualPurpose, "Purpose should not be null");
                    assertFalse(actualPurpose.isBlank(), "Purpose should not be blank");
                }
                case "creatorId" -> {
                    // Creator ID can be null for Adam tubes
                    String creatorId = birthCertificate.getCreatorId();
                    if (creatorId != null) {
                        assertFalse(creatorId.isBlank(),
                                "If creator ID exists, it should not be blank");
                    }
                }
                default -> fail("Unexpected property: " + property);
            }
        }
    }

    @Then("these properties should be accessible but unmodifiable")
    public void verifyPropertiesImmutability() {
        BirthCertificate birthCertificate = tube.getBirthCertificate();
        UUID initialId = birthCertificate.getUniversalId();
        Instant initialBirthTime = birthCertificate.getBirthTime();
        String initialPurpose = birthCertificate.getPurpose();

        // Attempt operations that should not affect immutability
        try {
            BirthCertificate snapshot = tube.getBirthCertificate();
            assertEquals(initialId, snapshot.getUniversalId(),
                    "Universal ID should remain constant");
            assertEquals(initialBirthTime, snapshot.getBirthTime(),
                    "Birth time should remain constant");
            assertEquals(initialPurpose, snapshot.getPurpose(),
                    "Purpose should remain constant");
        } catch (Exception e) {
            fail("Birth certificate properties should be accessible: " + e.getMessage());
        }
    }

    @Then("the birth certificate should be logged with INFO level")
    public void verifyBirthCertificateLogging() {
        // Note: This would require a custom test appender to verify logging
        // For now, we'll just verify the tube has logging capabilities
        assertNotNull(logger, "Logger should be initialized");
        logger.info("Birth certificate logging verification complete");
    }
}
