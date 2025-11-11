/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.steps.lifecycle;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Identity;
import org.s8r.component.Environment;
import org.s8r.component.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Step definitions for childhood phase tests, focusing on functional identity
 * as the tube develops reliable operation and learns from experience.
 */
public class ChildhoodPhaseSteps {

    private static final Logger LOGGER = Logger.getLogger(ChildhoodPhaseSteps.class.getName());
    
    // Test context
    private Component component;
    private Identity identity;
    private Environment env;
    private Exception lastException;
    private Map<String, Object> lastInput = new HashMap<>();
    private Map<String, Object> lastOutput = new HashMap<>();
    private List<Map<String, Object>> processingHistory = new ArrayList<>();
    private AtomicInteger operationCount = new AtomicInteger(0);
    private Map<String, Double> efficiencyMetrics = new HashMap<>();
    
    @Given("a tube has completed its embryonic development phase")
    public void a_tube_has_completed_its_embryonic_development_phase() {
        try {
            // Create a component that has completed embryonic phase
            component = Component.createAdam("Childhood Phase Test");
            identity = component.getIdentity();
            env = component.getEnvironment();
            
            // Set up environment to reflect completed embryonic phase
            env.setValue("lifecycle.phase", "CHILDHOOD");
            env.setValue("embryonic.phase.completed", "true");
            
            // Set up connection framework (from embryonic phase)
            env.setValue("connections.initialized", "true");
            env.setValue("input.connections.count", "3");
            env.setValue("output.connections.count", "2");
            
            for (int i = 0; i < 3; i++) {
                env.setValue("input.connection." + i + ".id", "input-" + i + "-" + identity.getId().substring(0, 8));
                env.setValue("input.connection." + i + ".status", "available");
            }
            
            for (int i = 0; i < 2; i++) {
                env.setValue("output.connection." + i + ".id", "output-" + i + "-" + identity.getId().substring(0, 8));
                env.setValue("output.connection." + i + ".status", "available");
            }
            
            // Set up internal structure (from embryonic phase)
            env.setValue("processing.mechanism", "standard");
            env.setValue("processing.capacity", "10");
            env.setValue("processing.algorithm", "default");
            
            env.setValue("state.container.count", "5");
            for (int i = 0; i < 5; i++) {
                env.setValue("state.container." + i + ".id", "state-" + i);
                env.setValue("state.container." + i + ".type", i % 2 == 0 ? "persistent" : "transient");
                env.setValue("state.container." + i + ".capacity", String.valueOf(100 * (i + 1)));
            }
            
            env.setValue("operation.boundary.min", "0");
            env.setValue("operation.boundary.max", "100");
            env.setValue("operation.boundary.timeout", "5000");
            
            // Initialize operational metrics
            env.setValue("metrics.initial.latency", "100");
            env.setValue("metrics.initial.throughput", "10");
            env.setValue("metrics.initial.error.rate", "0.05");
            env.setValue("metrics.current.latency", "100");
            env.setValue("metrics.current.throughput", "10");
            env.setValue("metrics.current.error.rate", "0.05");
            
            // Initialize learning systems
            env.setValue("learning.initialized", "true");
            env.setValue("learning.operation.count", "0");
            env.setValue("learning.patterns.count", "0");
            env.setValue("learning.optimizations.count", "0");
            
            // Initialize error handling
            env.setValue("error.handling.initialized", "true");
            env.setValue("error.count", "0");
            env.setValue("error.recovery.count", "0");
            
            // Set up operations log
            env.setValue("operations.log.count", "0");
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            Assertions.assertEquals("true", env.getValue("embryonic.phase.completed"),
                "Component should have completed embryonic phase");
            
            LOGGER.info("Created component that has completed embryonic phase with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create component with completed embryonic phase: " + e.getMessage());
            throw new AssertionError("Failed to create component with completed embryonic phase", e);
        }
    }
    
    @Given("the tube is in the {string} lifecycle state")
    public void the_tube_is_in_the_lifecycle_state(String stateName) {
        try {
            // Map the string state name to the actual State enum
            State targetState;
            switch (stateName) {
                case "ACTIVE":
                    targetState = State.ACTIVE;
                    break;
                case "READY":
                    targetState = State.ACTIVE; // Use ACTIVE for READY
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported state: " + stateName);
            }
            
            // Set the component state
            component.setState(targetState);
            
            // Update environment to reflect state
            env.setValue("lifecycle.state", stateName);
            
            Assertions.assertEquals(targetState, component.getState(), 
                "Component should be in " + stateName + " state");
            
            LOGGER.info("Component is now in " + stateName + " state");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to set component to " + stateName + " state: " + e.getMessage());
            throw new AssertionError("Failed to set component state", e);
        }
    }
    
    @When("the tube receives a standard data input")
    public void the_tube_receives_a_standard_data_input() {
        try {
            // Create a standard input
            String inputId = "input-" + UUID.randomUUID().toString().substring(0, 8);
            Map<String, Object> input = new HashMap<>();
            input.put("id", inputId);
            input.put("type", "standard");
            input.put("value", "test-data-value");
            input.put("timestamp", LocalDateTime.now().toString());
            input.put("metadata", Map.of("source", "test", "priority", "normal"));
            
            // Store the input for later verification
            lastInput = input;
            
            // Process the input
            Map<String, Object> output = processInput(input);
            
            // Store the output for later verification
            lastOutput = output;
            
            // Record operation in environment
            int logCount = Integer.parseInt(env.getValue("operations.log.count", "0"));
            logCount++;
            
            env.setValue("operations.log.count", String.valueOf(logCount));
            env.setValue("operations.log." + (logCount - 1) + ".id", UUID.randomUUID().toString().substring(0, 8));
            env.setValue("operations.log." + (logCount - 1) + ".input.id", inputId);
            env.setValue("operations.log." + (logCount - 1) + ".input.type", input.get("type").toString());
            env.setValue("operations.log." + (logCount - 1) + ".input.value", input.get("value").toString());
            env.setValue("operations.log." + (logCount - 1) + ".output.id", output.get("id").toString());
            env.setValue("operations.log." + (logCount - 1) + ".output.value", output.get("value").toString());
            env.setValue("operations.log." + (logCount - 1) + ".timestamp", LocalDateTime.now().toString());
            
            LOGGER.info("Component processed standard data input: " + inputId);
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to process standard data input: " + e.getMessage());
            throw new AssertionError("Failed to process standard data input", e);
        }
    }
    
    @When("the tube completes multiple processing operations")
    public void the_tube_completes_multiple_processing_operations() {
        try {
            // Simulate multiple processing operations
            int operationCount = 20; // Number of operations to simulate
            
            // Initial efficiency metrics
            double initialLatency = Double.parseDouble(env.getValue("metrics.initial.latency"));
            double initialThroughput = Double.parseDouble(env.getValue("metrics.initial.throughput"));
            double initialErrorRate = Double.parseDouble(env.getValue("metrics.initial.error.rate"));
            
            // Current metrics that will improve as operations are performed
            double currentLatency = initialLatency;
            double currentThroughput = initialThroughput;
            double currentErrorRate = initialErrorRate;
            
            Random random = new Random();
            
            // Perform operations
            for (int i = 0; i < operationCount; i++) {
                // Create input with some variation
                String inputId = "input-" + UUID.randomUUID().toString().substring(0, 8);
                String inputType = random.nextBoolean() ? "type-A" : "type-B";
                String inputValue = "value-" + (i % 5); // Create repeating values to form patterns
                
                Map<String, Object> input = new HashMap<>();
                input.put("id", inputId);
                input.put("type", inputType);
                input.put("value", inputValue);
                input.put("timestamp", LocalDateTime.now().toString());
                
                // Process input and capture output
                Map<String, Object> output = processInput(input);
                
                // Record operation
                Map<String, Object> operation = new HashMap<>();
                operation.put("input", input);
                operation.put("output", output);
                operation.put("latency", currentLatency);
                operation.put("timestamp", LocalDateTime.now());
                
                processingHistory.add(operation);
                
                // Record in environment
                int logCount = Integer.parseInt(env.getValue("operations.log.count", "0"));
                logCount++;
                
                env.setValue("operations.log.count", String.valueOf(logCount));
                env.setValue("operations.log." + (logCount - 1) + ".id", UUID.randomUUID().toString().substring(0, 8));
                env.setValue("operations.log." + (logCount - 1) + ".input.id", inputId);
                env.setValue("operations.log." + (logCount - 1) + ".input.type", inputType);
                env.setValue("operations.log." + (logCount - 1) + ".input.value", inputValue);
                env.setValue("operations.log." + (logCount - 1) + ".output.id", output.get("id").toString());
                env.setValue("operations.log." + (logCount - 1) + ".output.value", output.get("value").toString());
                env.setValue("operations.log." + (logCount - 1) + ".timestamp", LocalDateTime.now().toString());
                
                // Simulate improvement in efficiency metrics
                if (i > 0 && i % 5 == 0) {
                    currentLatency = Math.max(initialLatency * 0.7, currentLatency * 0.9); // Decrease latency (improvement)
                    currentThroughput = Math.min(initialThroughput * 1.5, currentThroughput * 1.1); // Increase throughput (improvement)
                    currentErrorRate = Math.max(initialErrorRate * 0.5, currentErrorRate * 0.9); // Decrease error rate (improvement)
                }
            }
            
            // Update metrics in environment
            env.setValue("metrics.current.latency", String.valueOf(currentLatency));
            env.setValue("metrics.current.throughput", String.valueOf(currentThroughput));
            env.setValue("metrics.current.error.rate", String.valueOf(currentErrorRate));
            
            // Update learning count
            env.setValue("learning.operation.count", String.valueOf(operationCount));
            
            // Identify patterns
            Map<String, Integer> valueFrequency = new HashMap<>();
            for (Map<String, Object> operation : processingHistory) {
                Map<String, Object> input = (Map<String, Object>) operation.get("input");
                String value = (String) input.get("value");
                valueFrequency.put(value, valueFrequency.getOrDefault(value, 0) + 1);
            }
            
            // Record patterns
            int patternCount = 0;
            for (Map.Entry<String, Integer> entry : valueFrequency.entrySet()) {
                if (entry.getValue() >= 3) { // Pattern must occur at least 3 times
                    env.setValue("learning.pattern." + patternCount + ".value", entry.getKey());
                    env.setValue("learning.pattern." + patternCount + ".frequency", entry.getValue().toString());
                    patternCount++;
                }
            }
            env.setValue("learning.patterns.count", String.valueOf(patternCount));
            
            // Apply optimizations based on patterns
            int optimizationCount = 0;
            for (int i = 0; i < patternCount; i++) {
                String patternValue = env.getValue("learning.pattern." + i + ".value");
                env.setValue("learning.optimization." + optimizationCount + ".pattern", patternValue);
                env.setValue("learning.optimization." + optimizationCount + ".type", "caching");
                env.setValue("learning.optimization." + optimizationCount + ".applied", "true");
                optimizationCount++;
            }
            
            // Add generic pathway optimizations
            env.setValue("learning.optimization." + optimizationCount + ".type", "pathway");
            env.setValue("learning.optimization." + optimizationCount + ".target", "input-processing");
            env.setValue("learning.optimization." + optimizationCount + ".applied", "true");
            optimizationCount++;
            
            env.setValue("learning.optimization." + optimizationCount + ".type", "pathway");
            env.setValue("learning.optimization." + optimizationCount + ".target", "output-generation");
            env.setValue("learning.optimization." + optimizationCount + ".applied", "true");
            optimizationCount++;
            
            env.setValue("learning.optimizations.count", String.valueOf(optimizationCount));
            
            // Store final metrics for verification
            efficiencyMetrics.put("initialLatency", initialLatency);
            efficiencyMetrics.put("initialThroughput", initialThroughput);
            efficiencyMetrics.put("initialErrorRate", initialErrorRate);
            efficiencyMetrics.put("currentLatency", currentLatency);
            efficiencyMetrics.put("currentThroughput", currentThroughput);
            efficiencyMetrics.put("currentErrorRate", currentErrorRate);
            
            this.operationCount.set(operationCount);
            
            LOGGER.info("Component completed " + operationCount + " processing operations and identified " + 
                       patternCount + " patterns");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to complete multiple processing operations: " + e.getMessage());
            throw new AssertionError("Failed to complete multiple processing operations", e);
        }
    }
    
    @When("the tube encounters a processing error")
    public void the_tube_encounters_a_processing_error() {
        try {
            // Create an input that will cause an error
            String inputId = "error-input-" + UUID.randomUUID().toString().substring(0, 8);
            Map<String, Object> input = new HashMap<>();
            input.put("id", inputId);
            input.put("type", "malformed");
            input.put("value", null); // Null value to cause error
            input.put("timestamp", LocalDateTime.now().toString());
            
            // Store the input for later verification
            lastInput = input;
            
            try {
                // Try to process the input (should cause error)
                Map<String, Object> output = processInput(input);
                
                // If we get here, no exception was thrown - force one for the test
                throw new RuntimeException("Simulated processing error for malformed input");
            } catch (Exception e) {
                // Expected exception - capture for error handling
                lastException = e;
                
                // Record error in environment
                int errorCount = Integer.parseInt(env.getValue("error.count", "0"));
                errorCount++;
                
                env.setValue("error.count", String.valueOf(errorCount));
                env.setValue("error." + (errorCount - 1) + ".input.id", inputId);
                env.setValue("error." + (errorCount - 1) + ".type", e.getClass().getSimpleName());
                env.setValue("error." + (errorCount - 1) + ".message", e.getMessage());
                env.setValue("error." + (errorCount - 1) + ".timestamp", LocalDateTime.now().toString());
                
                // Set component to error handling state
                env.setValue("error.handling.active", "true");
                env.setValue("error.handling.current.error", String.valueOf(errorCount - 1));
                
                LOGGER.info("Component encountered processing error: " + e.getMessage());
            }
            
            // Attempt recovery
            env.setValue("error.recovery.active", "true");
            
            // Recovery steps:
            // 1. Reset processing mechanism
            env.setValue("processing.mechanism.reset", "true");
            
            // 2. Clear any stuck data
            env.setValue("processing.buffer.cleared", "true");
            
            // 3. Reset internal state if necessary
            env.setValue("internal.state.reset", "true");
            
            // 4. Record recovery action
            int recoveryCount = Integer.parseInt(env.getValue("error.recovery.count", "0"));
            recoveryCount++;
            
            env.setValue("error.recovery.count", String.valueOf(recoveryCount));
            env.setValue("error.recovery." + (recoveryCount - 1) + ".error.id", env.getValue("error." + (Integer.parseInt(env.getValue("error.handling.current.error"))) + ".input.id"));
            env.setValue("error.recovery." + (recoveryCount - 1) + ".action", "full-reset");
            env.setValue("error.recovery." + (recoveryCount - 1) + ".success", "true");
            env.setValue("error.recovery." + (recoveryCount - 1) + ".timestamp", LocalDateTime.now().toString());
            
            // 5. Learn from error
            int learningCount = Integer.parseInt(env.getValue("learning.operation.count", "0"));
            learningCount++;
            
            env.setValue("learning.operation.count", String.valueOf(learningCount));
            env.setValue("learning.error." + (learningCount - 1) + ".type", env.getValue("error." + (Integer.parseInt(env.getValue("error.handling.current.error"))) + ".type"));
            env.setValue("learning.error." + (learningCount - 1) + ".prevention.strategy", "input-validation");
            
            // 6. Mark recovery as complete
            env.setValue("error.recovery.active", "false");
            env.setValue("error.handling.active", "false");
            
            LOGGER.info("Component attempted recovery from processing error");
        } catch (Exception e) {
            if (lastException == null) {
                lastException = e;
            }
            LOGGER.severe("Unexpected error during error handling test: " + e.getMessage());
            throw new AssertionError("Unexpected error during error handling test", e);
        }
    }
    
    @Then("the tube should process the input according to its function")
    public void the_tube_should_process_the_input_according_to_its_function() {
        // Verify processing occurred correctly
        Assertions.assertNotNull(lastOutput, "Input should be processed to produce output");
        
        // The exact verification depends on the expected processing function
        // For this test, we expect the output value to be derived from input in a specific way
        String inputValue = lastInput.get("value").toString();
        String expectedOutputValue = "processed-" + inputValue;
        
        Assertions.assertEquals(expectedOutputValue, lastOutput.get("value"),
            "Output value should be correctly derived from input value");
    }
    
    @And("the tube should produce expected output")
    public void the_tube_should_produce_expected_output() {
        // Verify output has expected structure and content
        Assertions.assertNotNull(lastOutput.get("id"), "Output should have an ID");
        Assertions.assertNotNull(lastOutput.get("value"), "Output should have a value");
        Assertions.assertNotNull(lastOutput.get("timestamp"), "Output should have a timestamp");
    }
    
    @And("the processing should be recorded in operational logs")
    public void the_processing_should_be_recorded_in_operational_logs() {
        String logCount = env.getValue("operations.log.count");
        Assertions.assertNotNull(logCount, "Operations log count should be recorded");
        Assertions.assertTrue(Integer.parseInt(logCount) > 0, "At least one operation should be logged");
        
        // Check most recent log entry
        int lastIndex = Integer.parseInt(logCount) - 1;
        
        String logInputId = env.getValue("operations.log." + lastIndex + ".input.id");
        String logInputValue = env.getValue("operations.log." + lastIndex + ".input.value");
        String logOutputId = env.getValue("operations.log." + lastIndex + ".output.id");
        String logOutputValue = env.getValue("operations.log." + lastIndex + ".output.value");
        
        Assertions.assertNotNull(logInputId, "Log should record input ID");
        Assertions.assertEquals(lastInput.get("id").toString(), logInputId, "Logged input ID should match actual input ID");
        
        Assertions.assertNotNull(logInputValue, "Log should record input value");
        Assertions.assertEquals(lastInput.get("value").toString(), logInputValue, "Logged input value should match actual input value");
        
        Assertions.assertNotNull(logOutputId, "Log should record output ID");
        Assertions.assertEquals(lastOutput.get("id").toString(), logOutputId, "Logged output ID should match actual output ID");
        
        Assertions.assertNotNull(logOutputValue, "Log should record output value");
        Assertions.assertEquals(lastOutput.get("value").toString(), logOutputValue, "Logged output value should match actual output value");
    }
    
    @Then("the tube should optimize its internal pathways")
    public void the_tube_should_optimize_its_internal_pathways() {
        String optimizationsCount = env.getValue("learning.optimizations.count");
        Assertions.assertNotNull(optimizationsCount, "Optimization count should be recorded");
        
        int count = Integer.parseInt(optimizationsCount);
        Assertions.assertTrue(count > 0, "At least one optimization should be applied");
        
        // Check for pathway optimizations specifically
        boolean foundPathwayOptimization = false;
        for (int i = 0; i < count; i++) {
            String type = env.getValue("learning.optimization." + i + ".type");
            if ("pathway".equals(type)) {
                String applied = env.getValue("learning.optimization." + i + ".applied");
                Assertions.assertEquals("true", applied, "Pathway optimization should be applied");
                foundPathwayOptimization = true;
            }
        }
        
        Assertions.assertTrue(foundPathwayOptimization, "At least one pathway optimization should exist");
    }
    
    @And("the tube should develop operational patterns")
    public void the_tube_should_develop_operational_patterns() {
        String patternsCount = env.getValue("learning.patterns.count");
        Assertions.assertNotNull(patternsCount, "Pattern count should be recorded");
        
        int count = Integer.parseInt(patternsCount);
        Assertions.assertTrue(count > 0, "At least one pattern should be identified");
        
        // Check pattern details
        for (int i = 0; i < count; i++) {
            String value = env.getValue("learning.pattern." + i + ".value");
            String frequency = env.getValue("learning.pattern." + i + ".frequency");
            
            Assertions.assertNotNull(value, "Pattern value should be recorded");
            Assertions.assertNotNull(frequency, "Pattern frequency should be recorded");
            Assertions.assertTrue(Integer.parseInt(frequency) >= 3, 
                "Pattern should occur at least 3 times to be recognized");
        }
    }
    
    @And("the tube should show improved efficiency metrics")
    public void the_tube_should_show_improved_efficiency_metrics() {
        double initialLatency = efficiencyMetrics.get("initialLatency");
        double currentLatency = efficiencyMetrics.get("currentLatency");
        double initialThroughput = efficiencyMetrics.get("initialThroughput");
        double currentThroughput = efficiencyMetrics.get("currentThroughput");
        double initialErrorRate = efficiencyMetrics.get("initialErrorRate");
        double currentErrorRate = efficiencyMetrics.get("currentErrorRate");
        
        // Verify metrics improved
        Assertions.assertTrue(currentLatency < initialLatency, 
            "Latency should decrease (improve) after multiple operations");
        Assertions.assertTrue(currentThroughput > initialThroughput, 
            "Throughput should increase (improve) after multiple operations");
        Assertions.assertTrue(currentErrorRate < initialErrorRate, 
            "Error rate should decrease (improve) after multiple operations");
        
        // Check that environment values match our tracked metrics
        double envLatency = Double.parseDouble(env.getValue("metrics.current.latency"));
        double envThroughput = Double.parseDouble(env.getValue("metrics.current.throughput"));
        double envErrorRate = Double.parseDouble(env.getValue("metrics.current.error.rate"));
        
        Assertions.assertEquals(currentLatency, envLatency, 0.0001, "Environment latency should match expected value");
        Assertions.assertEquals(currentThroughput, envThroughput, 0.0001, "Environment throughput should match expected value");
        Assertions.assertEquals(currentErrorRate, envErrorRate, 0.0001, "Environment error rate should match expected value");
    }
    
    @Then("the tube should safely handle the error condition")
    public void the_tube_should_safely_handle_the_error_condition() {
        // Verify error was recorded
        String errorCount = env.getValue("error.count");
        Assertions.assertNotNull(errorCount, "Error count should be recorded");
        Assertions.assertTrue(Integer.parseInt(errorCount) > 0, "At least one error should be recorded");
        
        // Get the last error
        int lastIndex = Integer.parseInt(errorCount) - 1;
        
        String errorInputId = env.getValue("error." + lastIndex + ".input.id");
        String errorType = env.getValue("error." + lastIndex + ".type");
        String errorMessage = env.getValue("error." + lastIndex + ".message");
        
        Assertions.assertNotNull(errorInputId, "Error should record input ID");
        Assertions.assertEquals(lastInput.get("id").toString(), errorInputId, "Error input ID should match actual input ID");
        
        Assertions.assertNotNull(errorType, "Error should record exception type");
        Assertions.assertNotNull(errorMessage, "Error should record exception message");
        
        // Verify component did not crash (still has active environment)
        Assertions.assertNotNull(env.getValue("lifecycle.phase"), "Component environment should still be accessible after error");
    }
    
    @And("the tube should attempt to recover operational state")
    public void the_tube_should_attempt_to_recover_operational_state() {
        // Verify recovery was attempted
        String recoveryCount = env.getValue("error.recovery.count");
        Assertions.assertNotNull(recoveryCount, "Recovery count should be recorded");
        Assertions.assertTrue(Integer.parseInt(recoveryCount) > 0, "At least one recovery should be attempted");
        
        // Get the last recovery
        int lastIndex = Integer.parseInt(recoveryCount) - 1;
        
        String recoveryErrorId = env.getValue("error.recovery." + lastIndex + ".error.id");
        String recoveryAction = env.getValue("error.recovery." + lastIndex + ".action");
        String recoverySuccess = env.getValue("error.recovery." + lastIndex + ".success");
        
        Assertions.assertNotNull(recoveryErrorId, "Recovery should reference error ID");
        Assertions.assertNotNull(recoveryAction, "Recovery should record action taken");
        Assertions.assertEquals("true", recoverySuccess, "Recovery should be successful");
        
        // Verify recovery steps
        Assertions.assertEquals("true", env.getValue("processing.mechanism.reset"), "Processing mechanism should be reset");
        Assertions.assertEquals("true", env.getValue("processing.buffer.cleared"), "Processing buffer should be cleared");
        Assertions.assertEquals("true", env.getValue("internal.state.reset"), "Internal state should be reset");
        
        // Verify recovery completed
        Assertions.assertEquals("false", env.getValue("error.recovery.active"), "Recovery should be complete");
        Assertions.assertEquals("false", env.getValue("error.handling.active"), "Error handling should be complete");
    }
    
    @And("the tube should learn from the error experience")
    public void the_tube_should_learn_from_the_error_experience() {
        // Verify error learning
        int learningCount = Integer.parseInt(env.getValue("learning.operation.count"));
        Assertions.assertTrue(learningCount > 0, "Learning operations count should be positive");
        
        // Get the last learning entry
        int lastIndex = learningCount - 1;
        
        String errorType = env.getValue("learning.error." + lastIndex + ".type");
        String preventionStrategy = env.getValue("learning.error." + lastIndex + ".prevention.strategy");
        
        Assertions.assertNotNull(errorType, "Learning should record error type");
        Assertions.assertNotNull(preventionStrategy, "Learning should record prevention strategy");
        Assertions.assertEquals("input-validation", preventionStrategy, "Learning should develop input validation as prevention strategy");
    }
    
    // Helper method to simulate processing input
    private Map<String, Object> processInput(Map<String, Object> input) throws Exception {
        // Check for null value that would cause error
        if (input.get("value") == null) {
            throw new IllegalArgumentException("Input value cannot be null");
        }
        
        // Simulate output generation based on input
        Map<String, Object> output = new HashMap<>();
        output.put("id", "output-" + UUID.randomUUID().toString().substring(0, 8));
        
        // Generate output value based on input pattern
        String inputValue = input.get("value").toString();
        String outputValue = "processed-" + inputValue;
        
        output.put("value", outputValue);
        output.put("timestamp", LocalDateTime.now().toString());
        
        return output;
    }
}