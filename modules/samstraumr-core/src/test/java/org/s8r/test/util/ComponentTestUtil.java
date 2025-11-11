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
package org.s8r.test.util;

import org.s8r.component.Component;
import org.s8r.component.State;
import org.s8r.test.util.ListenerFactory.RecordingEventListener;
import org.s8r.test.util.ListenerFactory.RecordingStateTransitionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Utility class for common component testing operations.
 * This provides helpful methods for setting up test components and performing common test operations.
 */
public class ComponentTestUtil {
    
    /**
     * Creates a test component with standardized initialization.
     *
     * @param reason The creation reason
     * @return A component initialized for testing
     */
    public static Component createTestComponent(String reason) {
        Component component = Component.createAdam(reason);
        
        // Initialize resource tracking (required for lifecycle tests)
        component.initializeResourceTracking();
        
        // Set the component to ACTIVE state (most tests start here)
        component.setState(State.ACTIVE);
        
        return component;
    }
    
    /**
     * Creates a test component with resource monitoring.
     *
     * @param reason The creation reason
     * @return A component initialized with resource monitoring
     */
    public static Component createResourceMonitoredComponent(String reason) {
        Component component = createTestComponent(reason);
        
        // Add specific resource configuration for monitoring tests
        component.setResourceUsage("memory", 50);     // Initial memory usage in MB
        component.setResourceUsage("threads", 2);     // Initial thread count
        component.setResourceUsage("connections", 0); // Initial connection count
        component.setResourceUsage("timers", 1);      // Initial timer count
        
        return component;
    }
    
    /**
     * Creates a component configured for event testing.
     *
     * @param reason The creation reason
     * @return A component that publishes and subscribes to events
     */
    public static Component createEventTestComponent(String reason) {
        Component component = createTestComponent(reason);
        
        // Create a self-listener for testing event functionality
        RecordingEventListener selfListener = ListenerFactory.createEventListener("*");
        component.addEventListener(selfListener, "*");
        
        return component;
    }
    
    /**
     * Attempts a full range of operations on the component to test different behaviors.
     *
     * @param component The component to test
     * @return Map of operation types to results
     */
    public static Map<String, Object> testAllOperations(Component component) {
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Test data processing
            results.put("processData", testOperation(component, c -> {
                Map<String, Object> testData = new HashMap<>();
                testData.put("testKey", "testValue");
                c.performOperation("process_data", testData);
            }));
            
            // Test status querying
            results.put("queryStatus", testOperation(component, c -> {
                c.performOperation("query_status", null);
            }));
            
            // Test configuration updates
            results.put("updateConfig", testOperation(component, c -> {
                Map<String, Object> config = new HashMap<>();
                config.put("testSetting", "newValue");
                c.performOperation("update_config", config);
            }));
            
            // Test connection establishment
            results.put("establishConnection", testOperation(component, c -> {
                c.establishConnection("testSource");
            }));
            
            // Test diagnostics
            results.put("runDiagnostics", testOperation(component, c -> {
                c.performOperation("run_diagnostics", null);
            }));
            
            // Test configuration reset
            results.put("resetConfig", testOperation(component, c -> {
                c.performOperation("reset_config", null);
            }));
            
            // Test configuration viewing
            results.put("viewConfig", testOperation(component, c -> {
                c.performOperation("view_config", null);
            }));
            
        } catch (Exception e) {
            results.put("exception", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Test whether an operation succeeds.
     *
     * @param component The component to test
     * @param operation The operation to perform
     * @return true if the operation succeeded, false if it failed
     */
    private static boolean testOperation(Component component, Consumer<Component> operation) {
        try {
            operation.accept(component);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verifies resource usage based on component state.
     *
     * @param component The component to check
     * @param resourceType The resource type to check
     * @param expectedMinimum The minimum expected value
     * @return true if the resource usage meets or exceeds the minimum
     */
    public static boolean verifyResourceAvailability(
            Component component, String resourceType, int expectedMinimum) {
        int actualUsage = component.getResourceUsage(resourceType);
        return actualUsage >= expectedMinimum;
    }
}