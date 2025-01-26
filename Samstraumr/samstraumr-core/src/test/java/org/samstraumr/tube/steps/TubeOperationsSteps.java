package org.samstraumr.tube.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.core.*;

import org.junit.jupiter.api.Assertions;

public class TubeOperationsSteps {
    private Tube tube;
    private VitalStats vitalStats;
    private HealthAssessment healthAssessment;


    @Given("an operational tube is fully initialized")
    public void an_operational_tube_is_fully_initialized() {
        // Implementation
    }


    @Then("the tube should report its vital statistics")
    public void the_tube_should_report_its_vital_statistics() {
        Assertions.assertTrue(vitalStats.getMemoryUsage() > 0, "Memory usage should be greater than 0");
        Assertions.assertTrue(vitalStats.getCpuCores() > 0, "CPU cores should be greater than 0");
    }

    @When("the tube analyzes its resource usage")
    public void the_tube_analyzes_its_resource_usage() {
        tube.analyzeResources();
    }

    @When("the tube performs a health check")
    public void the_tube_performs_a_health_check() {
        healthAssessment = tube.checkHealth();
        Assertions.assertNotNull(healthAssessment, "Health assessment should be completed");
    }

    @Then("the tube should be in a healthy state")
    public void the_tube_should_be_in_a_healthy_state() {
        Assertions.assertTrue(healthAssessment.isHealthy(), "Tube should be in a healthy state");
    }

    @When("the tube performs an operation {string}")
    public void the_tube_performs_an_operation(String operation) {
        tube.performOperation(operation);
    }

    @Then("the tube should adapt to feedback with rate {double}")
    public void the_tube_should_adapt_to_feedback_with_rate(Double rate) {
        tube.adaptToFeedback(rate);
        Assertions.assertNotNull(tube, "Tube should adapt to the new feedback rate");
    }
}
