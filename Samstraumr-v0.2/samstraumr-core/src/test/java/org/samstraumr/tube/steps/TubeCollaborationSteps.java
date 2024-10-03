package org.samstraumr.core.steps;

import static org.junit.jupiter.api.Assertions.*;
import org.samstraumr.core.Tube;
import org.samstraumr.core.Environment;
import org.samstraumr.core.TestHelper;
import io.cucumber.java.en.*;

import java.util.Map;

public class TubeCollaborationSteps {
    private Tube parentTube;
    private Tube childTube;
    private Tube connectedTube;

    @Given("a parent Tube with reason {string}")
    public void a_parent_Tube_with_reason(String reason) {
        parentTube = new Tube(reason, new Environment());
    }

    @And("a child Tube is created with reason {string} by the parent Tube")
    public void a_child_Tube_is_created_with_reason_by_the_parent_Tube(String reason) {
        childTube = new Tube(reason, new Environment(), parentTube);
    }

    @When("I check the child Tube's lineage")
    public void i_check_the_child_Tube_s_lineage() {
        // No action needed
    }

    @Then("it should include the parent Tube's reason {string}")
    public void it_should_include_the_parent_Tube_s_reason(String parentReason) {
        assertTrue(childTube.getLineage().contains(parentReason));
    }

    @Given("a Tube connected to another Tube with reason {string}")
    public void a_Tube_connected_to_another_Tube_with_reason(String connectedReason) {
        connectedTube = new Tube(connectedReason, new Environment());
        childTube.connectTo(connectedTube);
    }

    @When("I check the Tube's connections")
    public void i_check_the_Tube_s_connections() {
        // No action needed
    }

    @Then("the Tube should be aware of the connected Tube and its reason")
    public void the_Tube_should_be_aware_of_the_connected_Tube_and_its_reason() {
        Map<String, String> connections = childTube.getConnectedTubes();
        assertTrue(connections.containsKey(connectedTube.getUniqueId()));
        assertEquals(connectedTube.getReason(), connections.get(connectedTube.getUniqueId()));
    }

    @Given("a Tube with operational history")
    public void a_Tube_with_operational_history() {
        childTube = new Tube("Initial Reason", new Environment());
        childTube.recordOperation("Operation1", true);
        childTube.recordOperation("Operation2", false);
    }

    @When("the Tube analyzes its performance")
    public void the_Tube_analyzes_its_performance() {
        childTube.analyzePerformance();
    }

    @And("detects misalignment with its purpose")
    public void detects_misalignment_with_its_purpose() {
        // Logic within analyzePerformance
    }

    @Then("it should decide to evolve its purpose to {string}")
    public void it_should_decide_to_evolve_its_purpose_to(String newReason) {
        assertEquals(newReason, childTube.getReason());
        assertTrue(childTube.getLineage().contains(newReason));
    }

    @And("record the transformation in its lineage")
    public void record_the_transformation_in_its_lineage() {
        assertTrue(childTube.getLineage().contains("Initial Reason"));
        assertTrue(childTube.getLineage().contains(childTube.getReason()));
    }
}

