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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;
import org.s8r.component.identity.Identity;

/**
 * Utility class for bridging between old and new Component APIs.
 * 
 * <p>This class helps migrate tests from old Component API patterns to new ones
 * without requiring extensive code changes.
 */
public class ComponentCompatUtil {
    
    /**
     * Creates an "Adam" component (root component with no parent).
     * 
     * @param reason The reason for creating the component
     * @return A new Component instance
     */
    public static Component createAdam(String reason) {
        return Component.createAdam(reason);
    }
    
    /**
     * Creates a child component with the specified parent.
     * 
     * @param parent The parent component
     * @param reason The reason for creating the component
     * @return A new child Component instance
     */
    public static Component createChild(Component parent, String reason) {
        return parent.createChild(reason);
    }
    
    /**
     * Converts a string state name to a State enum value.
     * Handles both new State names and legacy LifecycleState names.
     * 
     * @param stateName The state name as a string
     * @return The corresponding State enum value
     */
    public static State convertState(String stateName) {
        if (stateName == null) {
            return State.READY; // Default state
        }
        
        try {
            return State.valueOf(stateName);
        } catch (IllegalArgumentException e) {
            // Handle legacy state names
            switch (stateName) {
                case "CONCEPTION_PHASE": return State.CONCEPTION;
                case "INITIALIZING_PHASE": return State.INITIALIZING;
                case "CONFIGURING_PHASE": return State.CONFIGURING;
                case "READY_PHASE": return State.READY;
                case "ACTIVE_PHASE": return State.ACTIVE;
                case "MAINTENANCE_PHASE": return State.MAINTENANCE;
                case "ERROR_PHASE": return State.ERROR;
                case "RECOVERING_PHASE": return State.RECOVERING;
                case "TERMINATING_PHASE": return State.TERMINATING;
                case "TERMINATED_PHASE": return State.TERMINATED;
                case "ARCHIVED_PHASE": return State.ARCHIVED;
                default: return State.READY; // Default to READY if unknown
            }
        }
    }
    
    /**
     * Transitions a component to the specified state.
     * 
     * @param component The component to transition
     * @param stateName The target state name (supports both new and legacy names)
     */
    public static void transitionTo(Component component, String stateName) {
        State targetState = convertState(stateName);
        component.setState(targetState);
    }
    
    /**
     * Checks if a component is in the specified state.
     * 
     * @param component The component to check
     * @param stateName The state name to check against (supports both new and legacy names)
     * @return true if the component is in the specified state
     */
    public static boolean isInState(Component component, String stateName) {
        State targetState = convertState(stateName);
        return component.getState() == targetState;
    }
    
    /**
     * Initializes a component with default settings.
     * 
     * @param component The component to initialize
     */
    public static void initializeComponent(Component component) {
        // Initialize resource tracking
        component.initializeResourceTracking();
        
        // Initialize environment with default values
        Environment env = component.getEnvironment();
        env.setParameter("test.initialized", "true");
        env.setParameter("test.timestamp", String.valueOf(System.currentTimeMillis()));
    }
    
    /**
     * Checks if a component has the specified capabilities based on its state.
     * 
     * @param component The component to check
     * @param capability The capability name to check
     * @return true if the component has the specified capability
     */
    public static boolean hasCapability(Component component, String capability) {
        State state = component.getState();
        
        switch (capability.toLowerCase()) {
            case "process_data":
                return state.allowsDataProcessing();
            case "configure":
                return state.allowsConfigurationChanges();
            case "create_children":
                return state == State.ACTIVE || state == State.READY;
            case "connect":
                return state.isOperational();
            default:
                return false;
        }
    }
    
    /**
     * Gets the parent component if available.
     * 
     * @param component The component to check
     * @return The parent component or null if there is no parent
     */
    public static Component getParent(Component component) {
        Identity identity = component.getIdentity();
        Identity parentIdentity = identity.getParentIdentity();
        
        if (parentIdentity == null) {
            return null;
        }
        
        // In a real implementation, this would use a registry to look up the parent component
        // This is a placeholder implementation
        return null;
    }
    
    /**
     * Executes a mock operation on the component based on capability.
     * 
     * @param component The component to operate on
     * @param operation The operation name
     * @param parameters The operation parameters
     * @return The operation result
     */
    public static Map<String, Object> executeOperation(Component component, String operation, Map<String, Object> parameters) {
        // Check if operation is allowed in current state
        if (!hasCapability(component, operation)) {
            throw new IllegalStateException("Operation " + operation + " not allowed in state " + component.getState());
        }
        
        // Mock operation execution
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("operation", operation);
        result.put("timestamp", System.currentTimeMillis());
        result.put("componentId", component.getIdentity().getUniqueId());
        
        return result;
    }
    
    /**
     * Validates component properties against expected values.
     * 
     * @param component The component to validate
     * @param expectedProperties Map of expected property names and values
     * @return true if all properties match expected values
     */
    public static boolean validateProperties(Component component, Map<String, Object> expectedProperties) {
        for (Map.Entry<String, Object> entry : expectedProperties.entrySet()) {
            Object actualValue = component.getProperty(entry.getKey());
            Object expectedValue = entry.getValue();
            
            if (!expectedValue.equals(actualValue)) {
                return false;
            }
        }
        
        return true;
    }
}