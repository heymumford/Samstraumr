package org.samstraumr.core.steps;

import static org.junit.jupiter.api.Assertions.*;
import org.samstraumr.core.Tube;
import org.samstraumr.core.Environment;
import org.samstraumr.core.TestHelper;
import io.cucumber.java.en.*;

public class TubeIdentitySteps {
    private Tube tube;
    private Environment environment;
    private String newReason;

    @Given("an environment with specific parameters")
    public void an_environment_with_specific_parameters() {
        environment = new Environment();
    }

    @When("a new Tube is created with the reason {string}")
    public void a_new_Tube_is_created_with_the_reason(String reason) {
        tube = new Tube(reason, environment);
    }

    @Then("the Tube should have a unique identifier derived from the environment")
    public void the_Tube_should_have_a_unique_identifier_derived_from_the_environment() {
        assertNotNull(tube.getUniqueId());
        // Additional checks can be added here
    }

    @And("the Tube's reason should be {string}")
    public void the_Tube_s_reason_should_be(String expectedReason) {
        assertEquals(expectedReason, tube.getReason());
    }

    @Given("an existing Tube with reason {string}")
    public void an_existing_Tube_with_reason(String reason) {
        tube = new Tube(reason, environment);
    }

    @And("the Tube detects misalignment with its environment")
    public void the_Tube_detects_misalignment_with_its_environment() {
        tube.detectMisalignment();
    }

    @When("the Tube evolves its purpose to {string}")
    public void the_Tube_evolves_its_purpose_to(String newReason) {
        this.newReason = newReason;
        tube.evolvePurpose(newReason);
    }

    @Then("the Tube's reason should be updated to {string}")
    public void the_Tube_s_reason_should_be_updated_to(String newReason) {
        assertEquals(newReason, tube.getReason());
    }

    @And("the Tube should record the transformation in its lineage")
    public void the_Tube_should_record_the_transformation_in_its_lineage() {
        assertTrue(tube.getLineage().contains("Initial Reason"));
        assertTrue(tube.getLineage().contains(newReason));
    }
}

