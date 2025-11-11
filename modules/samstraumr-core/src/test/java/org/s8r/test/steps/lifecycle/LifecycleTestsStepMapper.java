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
import org.s8r.component.State;
import org.s8r.component.Environment;

import java.util.logging.Logger;

/**
 * This class implements missing step definitions identified during test verification.
 * It serves as a direct mapper for steps that weren't properly matched by the verification system.
 */
public class LifecycleTestsStepMapper {

    private static final Logger LOGGER = Logger.getLogger(LifecycleTestsStepMapper.class.getName());
    
    // References to other step classes for delegation
    private final LifecycleTransitionSteps transitionSteps = new LifecycleTransitionSteps();
    private final ChildhoodPhaseSteps childhoodSteps = new ChildhoodPhaseSteps();
    private final InfancyPhaseSteps infancySteps = new InfancyPhaseSteps();
    
    // Shared test context
    private Component component;
    private State currentState;
    
    // Component transitions state implementation
    @When("the component transitions to {string} state")
    public void the_component_transitions_to_state(String stateName) {
        // Delegate to the existing implementation
        transitionSteps.the_component_transitions_to_state(stateName);
    }
    
    // Tube lifecycle state implementations
    @Given("the tube is in the {string} lifecycle state")
    public void the_tube_is_in_the_lifecycle_state(String state) {
        // Implementation or delegation to existing ChildhoodPhaseSteps
        if (childhoodSteps != null) {
            childhoodSteps.the_tube_is_in_the_lifecycle_state(state);
        } else {
            setupTubeInState(state);
        }
    }
    
    @Given("a tube in the {string} lifecycle state")
    public void a_tube_in_the_lifecycle_state(String state) {
        // Implementation or delegation to existing InfancyPhaseSteps
        if (infancySteps != null) {
            infancySteps.a_tube_in_the_lifecycle_state(state);
        } else {
            setupTubeInState(state);
        }
    }
    
    @When("the tube transitions from {string} to {string} state")
    public void the_tube_transitions_from_to_state(String fromState, String toState) {
        // Implementation or delegation to existing InfancyPhaseSteps
        if (infancySteps != null) {
            infancySteps.the_tube_transitions_from_to_state(fromState, toState);
        } else {
            // Basic implementation if delegation fails
            LOGGER.info("Transitioning tube from " + fromState + " to " + toState);
            
            // First make sure we're in the from state
            setupTubeInState(fromState);
            
            // Now transition to the next state
            try {
                component.setState(mapStateNameToEnum(toState));
                Assertions.assertEquals(mapStateNameToEnum(toState), component.getState(),
                    "Tube should be in " + toState + " state");
            } catch (Exception e) {
                LOGGER.severe("Failed to transition: " + e.getMessage());
                throw new AssertionError("Failed to transition states", e);
            }
        }
    }
    
    // Helper methods
    private void setupTubeInState(String stateName) {
        // Create a component if not already created
        if (component == null) {
            try {
                component = Component.createAdam("Lifecycle Test");
            } catch (Exception e) {
                LOGGER.severe("Failed to create component: " + e.getMessage());
                throw new AssertionError("Failed to create component", e);
            }
        }
        
        // Transition to the requested state
        try {
            State targetState = mapStateNameToEnum(stateName);
            component.setState(targetState);
            currentState = targetState;
            
            Assertions.assertEquals(targetState, component.getState(),
                "Component should be in " + stateName + " state");
        } catch (Exception e) {
            LOGGER.severe("Failed to set state: " + e.getMessage());
            throw new AssertionError("Failed to set component state", e);
        }
    }
    
    private State mapStateNameToEnum(String stateName) {
        // Map state names to actual State enum values
        switch (stateName.toUpperCase()) {
            case "ACTIVE": return State.ACTIVE;
            case "SUSPENDED": return State.SUSPENDED;
            case "MAINTENANCE": return State.MAINTENANCE;
            case "TERMINATED": return State.TERMINATED;
            case "CREATED": return State.CREATED;
            case "INITIALIZING": return State.INITIALIZING;
            case "CHILDHOOD": return State.ACTIVE; // Map lifecycle phases to closest system state
            case "INFANCY": return State.ACTIVE;
            default:
                LOGGER.warning("Unknown state name: " + stateName + ". Using ACTIVE as fallback.");
                return State.ACTIVE;
        }
    }
}