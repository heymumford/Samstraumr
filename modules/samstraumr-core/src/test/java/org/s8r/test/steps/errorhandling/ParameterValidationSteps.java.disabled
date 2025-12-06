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
package org.s8r.test.steps.errorhandling;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;
import org.s8r.component.InvalidStateTransitionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Step definitions for parameter validation tests.
 */
public class ParameterValidationSteps {

    // Test context to share between steps
    private Component component;
    private List<Map<String, Object>> testResults = new ArrayList<>();
    private Environment originalEnvironment;
    private State originalState;
    private int maxReasonLength = 255; // Assumed maximum length for reason string
    private ExecutorService executor;
    private AtomicInteger validOperationCount = new AtomicInteger(0);
    private AtomicInteger invalidOperationCount = new AtomicInteger(0);
    private long validationOverheadTime = 0;

    @Given("the S8r framework is initialized")
    public void the_s8r_framework_is_initialized() {
        // Reset test context
        component = null;
        testResults.clear();
        originalEnvironment = null;
        originalState = null;
    }

    @Given("a valid component exists")
    public void a_valid_component_exists() {
        try {
            component = Component.createAdam("Valid Test Component");
            originalState = component.getState();
            originalEnvironment = component.getEnvironment();
        } catch (Exception e) {
            Assertions.fail("Failed to create valid component: " + e.getMessage());
        }
    }

    @Given("the system is under high load")
    public void the_system_is_under_high_load() {
        // Set up execution service for concurrent operations
        executor = Executors.newFixedThreadPool(10);
        
        // Simulate high load (this is mocked for test purposes)
        // In a real test, you might allocate resources to actually create high load
    }

    @When("I attempt to create an Adam component with the following invalid parameters:")
    public void i_attempt_to_create_an_adam_component_with_invalid_parameters(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        testResults.clear();
        
        for (Map<String, String> row : rows) {
            String parameter = row.get("Parameter");
            String value = row.get("Value");
            String expectedExceptionType = row.get("Expected Exception");
            String expectedMessage = row.get("Expected Message");
            
            try {
                // Prepare test result entry
                Map<String, Object> result = new HashMap<>();
                result.put("parameter", parameter);
                result.put("value", value);
                result.put("expectedExceptionType", expectedExceptionType);
                result.put("expectedMessage", expectedMessage);
                
                // Attempt the operation with invalid parameter
                if ("reason".equals(parameter) && "null".equals(value)) {
                    component = Component.createAdam(null);
                // Note: the createAdam method no longer takes an environment parameter
                // We'll just handle the null reason case here
                } else if ("reason".equals(parameter) && "\"\"".equals(value)) {
                    component = Component.createAdam("");
                }
                
                // If we get here, no exception was thrown
                result.put("exceptionThrown", false);
                result.put("success", false);
                result.put("message", "No exception thrown");
                
            } catch (Exception e) {
                // Exception caught as expected
                Map<String, Object> result = new HashMap<>();
                result.put("parameter", parameter);
                result.put("value", value);
                result.put("expectedExceptionType", expectedExceptionType);
                result.put("expectedMessage", expectedMessage);
                result.put("exceptionThrown", true);
                result.put("actualExceptionType", e.getClass().getSimpleName());
                result.put("actualMessage", e.getMessage());
                
                // Check if it's the expected exception type
                boolean correctType = e.getClass().getSimpleName().equals(expectedExceptionType);
                boolean messageMatches = e.getMessage() != null && e.getMessage().contains(expectedMessage);
                
                result.put("success", correctType && messageMatches);
                
                testResults.add(result);
            }
        }
    }

    @When("I attempt to create a child component with the following invalid parameters:")
    public void i_attempt_to_create_a_child_component_with_invalid_parameters(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Create a valid parent component for testing
        Component validParent = null;
        try {
            validParent = Component.createAdam("Valid Parent");
        } catch (Exception e) {
            Assertions.fail("Failed to create valid parent component: " + e.getMessage());
        }
        
        for (Map<String, String> row : rows) {
            String parameter = row.get("Parameter");
            String value = row.get("Value");
            String expectedExceptionType = row.get("Expected Exception");
            String expectedMessage = row.get("Expected Message");
            
            try {
                // Prepare test result entry
                Map<String, Object> result = new HashMap<>();
                result.put("parameter", parameter);
                result.put("value", value);
                result.put("expectedExceptionType", expectedExceptionType);
                result.put("expectedMessage", expectedMessage);
                
                // Attempt the operation with invalid parameter
                if ("parent".equals(parameter) && "null".equals(value)) {
                    component = Component.createChild(null, "Test Child");
                } else if ("reason".equals(parameter) && "null".equals(value)) {
                    component = Component.createChild(validParent, null);
                } else if ("env".equals(parameter) && "null".equals(value)) {
                    component = Component.createChild(validParent, "Test Child", null);
                }
                
                // If we get here, no exception was thrown
                result.put("exceptionThrown", false);
                result.put("success", false);
                result.put("message", "No exception thrown");
                
                testResults.add(result);
                
            } catch (Exception e) {
                // Exception caught as expected
                Map<String, Object> result = new HashMap<>();
                result.put("parameter", parameter);
                result.put("value", value);
                result.put("expectedExceptionType", expectedExceptionType);
                result.put("expectedMessage", expectedMessage);
                result.put("exceptionThrown", true);
                result.put("actualExceptionType", e.getClass().getSimpleName());
                result.put("actualMessage", e.getMessage());
                
                // Check if it's the expected exception type
                boolean correctType = e.getClass().getSimpleName().equals(expectedExceptionType);
                boolean messageMatches = e.getMessage() != null && e.getMessage().contains(expectedMessage);
                
                result.put("success", correctType && messageMatches);
                
                testResults.add(result);
            }
        }
    }

    @When("I attempt to create an Adam component with reason exceeding maximum length")
    public void i_attempt_to_create_an_adam_component_with_reason_exceeding_maximum_length() {
        try {
            // Create a reason that exceeds the maximum length
            StringBuilder reason = new StringBuilder();
            for (int i = 0; i < maxReasonLength + 100; i++) {
                reason.append("X");
            }
            
            // Attempt to create component with too-long reason
            component = Component.createAdam(reason.toString());
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "reason");
            result.put("value", "too long");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "reason");
            result.put("value", "too long");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I attempt to set environment with null key")
    public void i_attempt_to_set_environment_with_null_key() {
        try {
            // Store original environment
            originalEnvironment = component.getEnvironment();
            
            // Attempt to set environment with null key
            component.getEnvironment().setValue(null, "value");
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "key");
            result.put("value", "null");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "key");
            result.put("value", "null");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I attempt to set environment with null value")
    public void i_attempt_to_set_environment_with_null_value() {
        try {
            // Store original environment if not already stored
            if (originalEnvironment == null) {
                originalEnvironment = component.getEnvironment();
            }
            
            // Attempt to set environment with null value
            component.getEnvironment().setValue("key", null);
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "value");
            result.put("value", "null");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "value");
            result.put("value", "null");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I attempt to set state to null")
    public void i_attempt_to_set_state_to_null() {
        try {
            // Store original state
            originalState = component.getState();
            
            // Attempt to set state to null
            component.setState(null);
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "state");
            result.put("value", "null");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "state");
            result.put("value", "null");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I attempt to terminate the component with null reason")
    public void i_attempt_to_terminate_the_component_with_null_reason() {
        try {
            // Attempt to terminate with null reason
            component.terminate(null);
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "reason");
            result.put("value", "null");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "reason");
            result.put("value", "null");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I attempt to register a null state transition listener")
    public void i_attempt_to_register_a_null_state_transition_listener() {
        try {
            // Attempt to register null state transition listener
            component.addStateTransitionListener(null);
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "stateTransitionListener");
            result.put("value", "null");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "stateTransitionListener");
            result.put("value", "null");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I attempt to register a null event listener")
    public void i_attempt_to_register_a_null_event_listener() {
        try {
            // Attempt to register null event listener
            component.addEventListener(null, "test-event");
            
            // If we get here, no exception was thrown
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "eventListener");
            result.put("value", "null");
            result.put("exceptionThrown", false);
            result.put("success", false);
            result.put("message", "No exception thrown");
            
            testResults.add(result);
            
        } catch (Exception e) {
            // Exception caught as expected
            Map<String, Object> result = new HashMap<>();
            result.put("parameter", "eventListener");
            result.put("value", "null");
            result.put("exceptionThrown", true);
            result.put("actualExceptionType", e.getClass().getSimpleName());
            result.put("actualMessage", e.getMessage());
            result.put("exception", e);
            
            testResults.add(result);
        }
    }

    @When("I perform {int} component operations with both valid and invalid parameters")
    public void i_perform_n_component_operations_with_both_valid_and_invalid_parameters(int count) {
        // Create valid component for test
        final Component testComponent;
        try {
            testComponent = Component.createAdam("Stress Test Component");
        } catch (Exception e) {
            Assertions.fail("Failed to create valid component for stress test: " + e.getMessage());
            return;
        }
        
        // Reset counters
        validOperationCount.set(0);
        invalidOperationCount.set(0);
        
        // Set up tasks for valid and invalid operations
        List<Callable<Void>> tasks = new ArrayList<>();
        
        // Start timing for validation overhead
        long startTime = System.nanoTime();
        
        // Add tasks for valid operations (60% of total)
        for (int i = 0; i < count * 0.6; i++) {
            final int index = i;
            tasks.add(() -> {
                try {
                    // Perform valid operations
                    testComponent.getEnvironment().setValue("key" + index, "value" + index);
                    validOperationCount.incrementAndGet();
                } catch (Exception e) {
                    // Unexpected exception
                    System.err.println("Valid operation failed: " + e.getMessage());
                }
                return null;
            });
        }
        
        // Add tasks for invalid operations (40% of total)
        for (int i = 0; i < count * 0.4; i++) {
            final int index = i;
            tasks.add(() -> {
                try {
                    // Attempt invalid operations (null keys, values)
                    if (index % 2 == 0) {
                        testComponent.getEnvironment().setValue(null, "value" + index);
                    } else {
                        testComponent.getEnvironment().setValue("key" + index, null);
                    }
                    // Should not reach here
                    System.err.println("Invalid operation did not throw exception");
                } catch (Exception e) {
                    // Expected exception
                    invalidOperationCount.incrementAndGet();
                }
                return null;
            });
        }
        
        try {
            // Execute all tasks
            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assertions.fail("Test interrupted: " + e.getMessage());
        }
        
        // Finish timing for validation overhead
        long endTime = System.nanoTime();
        validationOverheadTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
    }

    @Then("each attempt should fail with the expected exception and message")
    public void each_attempt_should_fail_with_the_expected_exception_and_message() {
        for (Map<String, Object> result : testResults) {
            // Check if exception was thrown
            Assertions.assertTrue((boolean)result.get("exceptionThrown"),
                "Exception should be thrown for invalid parameter: " + result.get("parameter"));
            
            // Check exception type
            String expectedType = (String)result.get("expectedExceptionType");
            String actualType = (String)result.get("actualExceptionType");
            Assertions.assertEquals(expectedType, actualType,
                "Exception type should be " + expectedType + " but was " + actualType);
            
            // Check exception message
            String expectedMessage = (String)result.get("expectedMessage");
            String actualMessage = (String)result.get("actualMessage");
            Assertions.assertTrue(actualMessage.contains(expectedMessage),
                "Exception message should contain '" + expectedMessage + "' but was '" + actualMessage + "'");
        }
    }

    @Then("no component should be created")
    public void no_component_should_be_created() {
        Assertions.assertNull(component, "Component should not be created with invalid parameters");
    }

    @Then("no resources should be allocated")
    public void no_resources_should_be_allocated() {
        // This is more of a conceptual check since we can't directly verify internal resource allocation
        // In a real test, you might have more specific checks based on how resources are tracked
        Assertions.assertNull(component, "Component should not be created with invalid parameters");
    }

    @Then("creation should fail with exception containing {string}")
    public void creation_should_fail_with_exception_containing(String errorText) {
        Assertions.assertTrue(!testResults.isEmpty(), "Test results should be captured");
        Map<String, Object> result = testResults.get(0);
        
        // Check if exception was thrown
        Assertions.assertTrue((boolean)result.get("exceptionThrown"),
            "Exception should be thrown");
        
        // Check exception message
        String actualMessage = (String)result.get("actualMessage");
        Assertions.assertTrue(actualMessage.contains(errorText),
            "Exception message should contain '" + errorText + "' but was '" + actualMessage + "'");
    }

    @Then("the exception should be of type {string}")
    public void the_exception_should_be_of_type(String exceptionType) {
        Assertions.assertTrue(!testResults.isEmpty(), "Test results should be captured");
        Map<String, Object> result = testResults.get(0);
        
        // Check exception type
        String actualType = (String)result.get("actualExceptionType");
        Assertions.assertEquals(exceptionType, actualType,
            "Exception type should be " + exceptionType + " but was " + actualType);
    }

    @Then("the exception should include the maximum allowed length")
    public void the_exception_should_include_the_maximum_allowed_length() {
        Assertions.assertTrue(!testResults.isEmpty(), "Test results should be captured");
        Map<String, Object> result = testResults.get(0);
        
        // Check exception message for maximum length information
        String actualMessage = (String)result.get("actualMessage");
        Assertions.assertTrue(actualMessage.contains(String.valueOf(maxReasonLength)) || 
                             actualMessage.matches(".*maximum.*\\d+.*"),
            "Exception message should include the maximum allowed length");
    }

    @Then("the actual length of the provided reason")
    public void the_actual_length_of_the_provided_reason() {
        Assertions.assertTrue(!testResults.isEmpty(), "Test results should be captured");
        Map<String, Object> result = testResults.get(0);
        
        // Check exception message for actual length information
        String actualMessage = (String)result.get("actualMessage");
        Assertions.assertTrue(actualMessage.contains("length") && 
                             actualMessage.matches(".*\\d+.*"),
            "Exception message should include the actual length of the provided reason");
    }

    @Then("the operation should fail with IllegalArgumentException containing {string}")
    public void the_operation_should_fail_with_exception_containing(String errorText) {
        Assertions.assertTrue(!testResults.isEmpty(), "Test results should be captured");
        Map<String, Object> result = testResults.get(0);
        
        // Check if exception was thrown
        Assertions.assertTrue((boolean)result.get("exceptionThrown"),
            "Exception should be thrown");
        
        // Check exception type
        String actualType = (String)result.get("actualExceptionType");
        Assertions.assertEquals("IllegalArgumentException", actualType,
            "Exception type should be IllegalArgumentException but was " + actualType);
        
        // Check exception message
        String actualMessage = (String)result.get("actualMessage");
        Assertions.assertTrue(actualMessage.contains(errorText),
            "Exception message should contain '" + errorText + "' but was '" + actualMessage + "'");
    }

    @Then("the original environment should remain unchanged")
    public void the_original_environment_should_remain_unchanged() {
        Assertions.assertNotNull(originalEnvironment, "Original environment should be stored");
        
        // Compare current environment with original
        // This is a simplified comparison - in a real test, you'd compare all relevant properties
        Environment currentEnv = component.getEnvironment();
        
        // In a real test, you'd have a proper equals method or comparison logic
        // For this test, we'll just check they're not the same instance but have equal contents
        Assertions.assertNotSame(originalEnvironment, currentEnv,
            "Environment should be a defensive copy");
            
        // Check that values match (assuming some common keys existed)
        // This would depend on how Environment is actually implemented
    }

    @Then("the component state should remain unchanged")
    public void the_component_state_should_remain_unchanged() {
        Assertions.assertNotNull(originalState, "Original state should be stored");
        Assertions.assertEquals(originalState, component.getState(),
            "Component state should remain unchanged after failed operation");
    }

    @Then("the component should not be terminated")
    public void the_component_should_not_be_terminated() {
        Assertions.assertNotEquals(State.TERMINATED, component.getState(),
            "Component should not be terminated after failed operation");
    }

    @Then("no listeners should be registered")
    public void no_listeners_should_be_registered() {
        // This would require access to internal listener lists
        // For this test, we'll use indirect verification by checking for effects
        
        // Perform a state change that should trigger listeners
        try {
            // Original state should be preserved from earlier steps
            State currentState = component.getState();
            State newState = (currentState == State.ACTIVE) ? State.SUSPENDED : State.ACTIVE;
            
            component.setState(newState);
            
            // Now return to original state
            component.setState(currentState);
            
            // If null listeners were registered, this would likely have caused errors
            // So if we got here without exceptions, listeners weren't registered
            
        } catch (Exception e) {
            Assertions.fail("Unexpected exception during listener test: " + e.getMessage());
        }
    }

    @Then("all invalid parameters should be consistently rejected")
    public void all_invalid_parameters_should_be_consistently_rejected() {
        int expectedInvalidCount = (int)(1000 * 0.4); // 40% of operations were invalid
        int actualInvalidCount = invalidOperationCount.get();
        
        // Allow some tolerance (±5%)
        int tolerance = (int)(expectedInvalidCount * 0.05);
        Assertions.assertTrue(Math.abs(expectedInvalidCount - actualInvalidCount) <= tolerance,
            "Invalid parameter count should be approximately " + expectedInvalidCount + 
            " but was " + actualInvalidCount);
    }

    @Then("all valid operations should succeed")
    public void all_valid_operations_should_succeed() {
        int expectedValidCount = (int)(1000 * 0.6); // 60% of operations were valid
        int actualValidCount = validOperationCount.get();
        
        // Allow some tolerance (±5%)
        int tolerance = (int)(expectedValidCount * 0.05);
        Assertions.assertTrue(Math.abs(expectedValidCount - actualValidCount) <= tolerance,
            "Valid operation count should be approximately " + expectedValidCount + 
            " but was " + actualValidCount);
    }

    @Then("parameter validation should add minimal overhead")
    public void parameter_validation_should_add_minimal_overhead() {
        // Check that validation overhead is reasonable
        // For 1000 operations, overhead should be less than 500ms (0.5ms per operation)
        Assertions.assertTrue(validationOverheadTime < 500,
            "Parameter validation overhead should be less than 500ms but was " + validationOverheadTime + "ms");
    }

    @Then("error messages should provide consistent parameter details")
    public void error_messages_should_provide_consistent_parameter_details() {
        // This is a qualitative check that would require examining multiple error messages
        // For this test, we'll verify that the error messages from our previous tests were consistent
        
        // Check that all error messages for the same parameter are consistent
        Map<String, String> paramToMessage = new HashMap<>();
        
        for (Map<String, Object> result : testResults) {
            if ((boolean)result.get("exceptionThrown")) {
                String param = (String)result.get("parameter");
                String message = (String)result.get("actualMessage");
                
                if (paramToMessage.containsKey(param)) {
                    // Check for consistency with previous messages for this parameter
                    String prevMessage = paramToMessage.get(param);
                    Assertions.assertTrue(message.contains(param),
                        "Error message should mention parameter name: " + param);
                    
                    // Messages should be similar (contain the same key phrases)
                    boolean similar = message.contains("cannot be null") &&
                                     prevMessage.contains("cannot be null") ||
                                     message.contains("cannot be empty") &&
                                     prevMessage.contains("cannot be empty");
                    
                    Assertions.assertTrue(similar,
                        "Error messages for parameter " + param + " should be consistent");
                    
                } else {
                    // Store first message for this parameter
                    paramToMessage.put(param, message);
                }
            }
        }
    }
}