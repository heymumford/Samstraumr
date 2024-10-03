package org.samstraumr.core.steps;

import static org.junit.jupiter.api.Assertions.*;
import org.samstraumr.core.Tube;
import org.samstraumr.core.Environment;
import org.samstraumr.core.TestHelper;
import io.cucumber.java.en.*;

import java.util.List;

public class TubeResilienceSteps {
    private Tube tube;
    private List<Tube> replicas;

    @Given("a Tube operating under normal conditions")
    public void a_Tube_operating_under_normal_conditions() {
        tube = new Tube("Normal Operation", new Environment());
    }

    @When("the Tube detects dangerous conditions")
    public void the_Tube_detects_dangerous_conditions() {
        tube.detectDangerousConditions();
    }

    @Then("it should pause or terminate operations")
    public void it_should_pause_or_terminate_operations() {
        assertTrue(tube.isPaused() || tube.isTerminated());
    }

    @And("notify connected tubes of its status change")
    public void notify_connected_tubes_of_its_status_change() {
        // Logic to verify connected tubes are notified
    }

    @Given("a Tube that has detected misalignment with its purpose")
    public void a_Tube_that_has_detected_misalignment_with_its_purpose() {
        tube = new Tube("Old Purpose", new Environment());
        tube.detectMisalignment();
    }

    @When("the Tube decides to transform into a new entity with reason {string}")
    public void the_Tube_decides_to_transform_into_a_new_entity_with_reason(String newReason) {
        tube.transformIntoNewEntity(newReason);
    }

    @Then("it should create a new Tube with the updated reason")
    public void it_should_create_a_new_Tube_with_the_updated_reason() {
        Tube newTube = tube.getTransformedTube();
        assertNotNull(newTube);
        assertEquals(newReason, newTube.getReason());
    }

    @And("gracefully terminate its own operations")
    public void gracefully_terminate_its_own_operations() {
        assertTrue(tube.isTerminated());
    }

    @And("the new Tube should retain the lineage of the original Tube")
    public void the_new_Tube_should_retain_the_lineage_of_the_original_Tube() {
        Tube newTube = tube.getTransformedTube();
        assertTrue(newTube.getLineage().containsAll(tube.getLineage()));
    }

    @Given("a Tube experiencing increased workload")
    public void a_Tube_experiencing_increased_workload() {
        tube = new Tube("Handle Workload", new Environment());
        tube.setCurrentWorkload(150);
    }

    @When("the Tube detects that scaling is required")
    public void the_Tube_detects_that_scaling_is_required() {
        tube.detectScalingNeed();
    }

    @Then("it should replicate itself to handle the workload")
    public void it_should_replicate_itself_to_handle_the_workload() {
        replicas = tube.getReplicas();
        assertFalse(replicas.isEmpty());
    }

    @And("the replicas should share the workload appropriately")
    public void the_replicas_should_share_the_workload_appropriately() {
        int totalWorkload = tube.getCurrentWorkload();
        int expectedWorkloadPerTube = totalWorkload / (replicas.size() + 1);
        assertEquals(expectedWorkloadPerTube, tube.getAssignedWorkload());
        for (Tube replica : replicas) {
            assertEquals(expectedWorkloadPerTube, replica.getAssignedWorkload());
        }
    }
}

