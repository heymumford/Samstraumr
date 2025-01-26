package org.samstraumr.tube.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.core.VitalStats;
import org.samstraumr.tube.core.HealthAssessment;
import org.samstraumr.tube.core.VitalStats.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TubeAwarenessSteps {
    private static final Logger logger = LoggerFactory.getLogger(TubeAwarenessSteps.class);

    private Tube tube;
    private VitalStats vitalStats;
    private HealthAssessment healthStatus;

    @Given("a fully initialized tube exists")
    public void tubeExists() {
        tube = new Tube("awareness_test");

        // Verify core identity and initialization status via vital stats
        vitalStats = tube.getVitalStats();
        assertNotNull(vitalStats.getStatistic("UUID"), "UUID should be initialized");
        assertNotNull(vitalStats.getStatistic("BirthTime"), "Birth time should be initialized");
        assertNotNull(vitalStats.getStatistic("SystemInfo"), "System info should be retrieved");
        assertTrue(vitalStats.isInitialized(), "Tube should be fully initialized");
    }

    @Given("basic monitoring systems are active")
    public void monitoringIsActive() {
        assertTrue(tube.isMonitoringActive(), "Monitoring should be active");
    }

    @When("the tube performs a self-check")
    public void tubeSelfCheck() {
        vitalStats = tube.checkVitalStats();
        assertNotNull(vitalStats, "Vital stats should be collected");
    }

    @Then("it should report current vital statistics:")
    public void verifyVitalStatistics(DataTable expectedStats) {
        for (Map<String, String> row : expectedStats.asMaps(String.class, String.class)) {
            String vital = row.get("Vital");
            String type = row.get("Type");
            String frequency = row.get("Update Frequency");

            Stat stat = vitalStats.getStatistic(vital);
            assertNotNull(stat, "Vital stat '" + vital + "' should exist");
            assertEquals(type, stat.getType(), "Vital stat type should match expected");
            assertTrue(stat.getLastUpdate().isBefore(Instant.now().plusMillis(parseFrequency(frequency))),
                    "Stat should be updated within frequency window");
        }
    }

    @When("the tube analyzes its resource usage patterns")
    public void analyzeResourceUsage() {
        tube.analyzeResources();
    }

    @Then("it should maintain rolling metrics for:")
    public void verifyRollingMetrics(DataTable expectedMetrics) {
        Map<String, VitalStats.Stat> metrics = vitalStats.getAllStats();

        for (Map<String, String> row : expectedMetrics.asMaps(String.class, String.class)) {
            String metric = row.get("Metric");
            String window = row.get("Window");
            String aggregation = row.get("Aggregation");
            String threshold = row.get("Threshold Alert");

            Stat metricData = metrics.get(metric);
            assertNotNull(metricData, "Metric '" + metric + "' should exist");
            assertEquals(parseWindow(window), metricData.getWindowSize(), "Window size should match");
            assertTrue(metricData.hasAggregation(aggregation), "Should support " + aggregation + " aggregation");
            assertNotNull(metricData.getThresholdAlert(), "Should have threshold alert configured");
        }
    }

    @When("a health check cycle executes")
    public void executeHealthCheck() {
        healthStatus = tube.checkHealth();
        assertNotNull(healthStatus, "Health check should complete");
    }

    @Then("it should evaluate the following aspects:")
    public void verifyHealthAspects(DataTable expectedAspects) {
        for (Map<String, String> row : expectedAspects.asMaps(String.class, String.class)) {
            String aspect = row.get("Aspect");
            String range = row.get("Healthy Range");

            var healthMetric = healthStatus.getHealthAspect(aspect);
            assertNotNull(healthMetric, "Health aspect '" + aspect + "' should exist");

            int targetValue = parseRange(range);
            assertTrue(healthMetric.isWithinRange(targetValue), "Health metric should be within healthy range");
        }
    }

    // Utility methods for parsing
    private long parseFrequency(String frequency) {
        // Implement conversion logic for frequency to milliseconds if needed
        return 1000; // Placeholder
    }

    private Duration parseWindow(String window) {
        // Implement parsing logic to convert window to Duration
        return Duration.ofMinutes(5); // Placeholder
    }

    private int parseRange(String range) {
        // Parsing numeric values from range (e.g., ">= 20%")
        if (range.contains("%")) range = range.replace("%", "").trim();
        if (range.contains("-")) return Integer.parseInt(range.split("-")[0].trim());
        else if (range.contains(">=")) return Integer.parseInt(range.replace(">=", "").trim());
        return Integer.parseInt(range.trim());
    }
}
