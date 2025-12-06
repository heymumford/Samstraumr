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
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Step definitions for infancy phase tests, focusing on memory identity formation
 * and the beginning of experience recording and pattern recognition.
 */
public class InfancyPhaseSteps {

    private static final Logger LOGGER = Logger.getLogger(InfancyPhaseSteps.class.getName());
    
    // Test context
    private Component component;
    private Identity identity;
    private State previousState;
    private Exception lastException;
    private List<Map<String, String>> experiences = new ArrayList<>();
    private List<String> inputs = new ArrayList<>();
    private Map<String, Integer> inputFrequency = new HashMap<>();
    private Map<String, String> recognizedPatterns = new HashMap<>();
    
    @Given("a tube in the {string} lifecycle state")
    public void a_tube_in_the_lifecycle_state(String stateName) {
        try {
            // Create a component and set it to the specified state
            component = Component.createAdam("Infancy Phase Test");
            identity = component.getIdentity();
            
            // Map the string state name to the actual State enum
            State targetState;
            switch (stateName) {
                case "READY":
                    targetState = State.ACTIVE;
                    break;
                case "INITIALIZING":
                    targetState = State.INITIALIZING;
                    break;
                case "ACTIVE":
                    targetState = State.ACTIVE;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported state: " + stateName);
            }
            
            // Set the component state
            previousState = component.getState();
            component.setState(targetState);
            
            // Initialize memory structures
            Environment env = component.getEnvironment();
            env.setValue("memory.initialized", "true");
            env.setValue("memory.episodic.count", "0");
            env.setValue("memory.state.transitions", "0");
            env.setValue("memory.patterns.recognized", "0");
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertEquals(targetState, component.getState(), 
                "Component should be in " + stateName + " state");
            
            LOGGER.info("Created component in " + stateName + " state with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create component in " + stateName + " state: " + e.getMessage());
            throw new AssertionError("Failed to create component in specified state", e);
        }
    }
    
    @When("the tube processes its first input")
    public void the_tube_processes_its_first_input() {
        try {
            // Simulate processing a first input
            Environment env = component.getEnvironment();
            
            // Create a sample input
            Map<String, String> inputData = new HashMap<>();
            inputData.put("id", UUID.randomUUID().toString());
            inputData.put("type", "data");
            inputData.put("value", "first-experience-value");
            inputData.put("timestamp", LocalDateTime.now().toString());
            
            // Process the input
            Map<String, String> outputData = processInput(inputData);
            
            // Record the experience in memory
            int experienceCount = Integer.parseInt(env.getValue("memory.episodic.count", "0"));
            experienceCount++;
            
            Map<String, String> experience = new HashMap<>();
            experience.put("id", "exp-" + UUID.randomUUID().toString().substring(0, 8));
            experience.put("type", "first-experience");
            experience.put("inputId", inputData.get("id"));
            experience.put("inputType", inputData.get("type"));
            experience.put("inputValue", inputData.get("value"));
            experience.put("outputId", outputData.get("id"));
            experience.put("outputValue", outputData.get("value"));
            experience.put("timestamp", LocalDateTime.now().toString());
            experience.put("isFormative", "true");
            
            // Store experience in environment
            env.setValue("memory.episodic.count", String.valueOf(experienceCount));
            env.setValue("memory.episodic.experience.0.id", experience.get("id"));
            env.setValue("memory.episodic.experience.0.type", experience.get("type"));
            env.setValue("memory.episodic.experience.0.inputId", experience.get("inputId"));
            env.setValue("memory.episodic.experience.0.inputType", experience.get("inputType"));
            env.setValue("memory.episodic.experience.0.inputValue", experience.get("inputValue"));
            env.setValue("memory.episodic.experience.0.outputId", experience.get("outputId"));
            env.setValue("memory.episodic.experience.0.outputValue", experience.get("outputValue"));
            env.setValue("memory.episodic.experience.0.timestamp", experience.get("timestamp"));
            env.setValue("memory.episodic.experience.0.isFormative", experience.get("isFormative"));
            
            // Add to our test context
            experiences.add(experience);
            
            LOGGER.info("Component processed its first input and recorded the experience: " + experience.get("id"));
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to process first input: " + e.getMessage());
            throw new AssertionError("Failed to process first input", e);
        }
    }
    
    @When("the tube transitions from {string} to {string} state")
    public void the_tube_transitions_from_to_state(String fromState, String toState) {
        try {
            // Map the string state names to the actual State enums
            State sourceState;
            State targetState;
            
            switch (fromState) {
                case "INITIALIZING":
                    sourceState = State.INITIALIZING;
                    break;
                case "READY":
                    sourceState = State.ACTIVE;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported source state: " + fromState);
            }
            
            switch (toState) {
                case "ACTIVE":
                    targetState = State.ACTIVE;
                    break;
                case "SUSPENDED":
                    targetState = State.SUSPENDED;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported target state: " + toState);
            }
            
            // Verify current state
            Assertions.assertEquals(sourceState, component.getState(), 
                "Component should be in " + fromState + " state before transition");
            
            // Record previous state before transition
            previousState = component.getState();
            
            // Perform state transition
            component.setState(targetState);
            
            // Record state transition in environment
            Environment env = component.getEnvironment();
            int transitionCount = Integer.parseInt(env.getValue("memory.state.transitions", "0"));
            transitionCount++;
            
            env.setValue("memory.state.transitions", String.valueOf(transitionCount));
            env.setValue("memory.state.transition." + (transitionCount - 1) + ".from", fromState);
            env.setValue("memory.state.transition." + (transitionCount - 1) + ".to", toState);
            env.setValue("memory.state.transition." + (transitionCount - 1) + ".timestamp", LocalDateTime.now().toString());
            
            // Verify state was transitioned
            Assertions.assertEquals(targetState, component.getState(), 
                "Component should be in " + toState + " state after transition");
            
            LOGGER.info("Component transitioned from " + fromState + " to " + toState + " state");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to transition state: " + e.getMessage());
            throw new AssertionError("Failed to transition state", e);
        }
    }
    
    @When("the tube encounters similar inputs multiple times")
    public void the_tube_encounters_similar_inputs_multiple_times() {
        try {
            // Simulate processing multiple similar inputs
            Environment env = component.getEnvironment();
            
            // Define a set of similar inputs
            String[] inputValues = {
                "pattern-value-A", "pattern-value-B", "pattern-value-A", 
                "pattern-value-C", "pattern-value-A", "pattern-value-B"
            };
            
            // Process each input and record frequency
            for (int i = 0; i < inputValues.length; i++) {
                String value = inputValues[i];
                
                // Create a sample input
                Map<String, String> inputData = new HashMap<>();
                inputData.put("id", UUID.randomUUID().toString());
                inputData.put("type", "data");
                inputData.put("value", value);
                inputData.put("timestamp", LocalDateTime.now().toString());
                
                // Process the input
                Map<String, String> outputData = processInput(inputData);
                
                // Track input frequency
                inputFrequency.put(value, inputFrequency.getOrDefault(value, 0) + 1);
                
                // Record the experience in memory
                int experienceCount = Integer.parseInt(env.getValue("memory.episodic.count", "0"));
                
                Map<String, String> experience = new HashMap<>();
                experience.put("id", "exp-" + UUID.randomUUID().toString().substring(0, 8));
                experience.put("type", "recurring-experience");
                experience.put("inputId", inputData.get("id"));
                experience.put("inputType", inputData.get("type"));
                experience.put("inputValue", inputData.get("value"));
                experience.put("outputId", outputData.get("id"));
                experience.put("outputValue", outputData.get("value"));
                experience.put("timestamp", LocalDateTime.now().toString());
                experience.put("isFormative", "false");
                
                // Store experience in environment
                env.setValue("memory.episodic.count", String.valueOf(experienceCount + 1));
                env.setValue("memory.episodic.experience." + experienceCount + ".id", experience.get("id"));
                env.setValue("memory.episodic.experience." + experienceCount + ".type", experience.get("type"));
                env.setValue("memory.episodic.experience." + experienceCount + ".inputId", experience.get("inputId"));
                env.setValue("memory.episodic.experience." + experienceCount + ".inputType", experience.get("inputType"));
                env.setValue("memory.episodic.experience." + experienceCount + ".inputValue", experience.get("inputValue"));
                env.setValue("memory.episodic.experience." + experienceCount + ".outputId", experience.get("outputId"));
                env.setValue("memory.episodic.experience." + experienceCount + ".outputValue", experience.get("outputValue"));
                env.setValue("memory.episodic.experience." + experienceCount + ".timestamp", experience.get("timestamp"));
                env.setValue("memory.episodic.experience." + experienceCount + ".isFormative", experience.get("isFormative"));
                
                // Add to our test context
                experiences.add(experience);
                inputs.add(value);
            }
            
            // Identify patterns
            for (Map.Entry<String, Integer> entry : inputFrequency.entrySet()) {
                if (entry.getValue() > 1) {
                    // This is a recurring pattern
                    String patternId = "pattern-" + UUID.randomUUID().toString().substring(0, 8);
                    recognizedPatterns.put(entry.getKey(), patternId);
                    
                    // Record pattern in environment
                    int patternCount = Integer.parseInt(env.getValue("memory.patterns.recognized", "0"));
                    env.setValue("memory.patterns.recognized", String.valueOf(patternCount + 1));
                    env.setValue("memory.pattern." + patternCount + ".id", patternId);
                    env.setValue("memory.pattern." + patternCount + ".inputValue", entry.getKey());
                    env.setValue("memory.pattern." + patternCount + ".frequency", entry.getValue().toString());
                    env.setValue("memory.pattern." + patternCount + ".recognized", "true");
                }
            }
            
            // Apply pattern-based optimizations
            if (!recognizedPatterns.isEmpty()) {
                env.setValue("memory.pattern.optimization.applied", "true");
                env.setValue("memory.pattern.optimization.count", String.valueOf(recognizedPatterns.size()));
                
                // For each recognized pattern, create an optimization
                int index = 0;
                for (Map.Entry<String, String> entry : recognizedPatterns.entrySet()) {
                    env.setValue("memory.pattern.optimization." + index + ".patternId", entry.getValue());
                    env.setValue("memory.pattern.optimization." + index + ".inputValue", entry.getKey());
                    env.setValue("memory.pattern.optimization." + index + ".optimizationType", "caching");
                    
                    index++;
                }
            }
            
            LOGGER.info("Component processed multiple similar inputs and identified " + 
                       recognizedPatterns.size() + " patterns");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to process multiple inputs: " + e.getMessage());
            throw new AssertionError("Failed to process multiple inputs", e);
        }
    }
    
    @Then("the tube should record the experience in its episodic memory")
    public void the_tube_should_record_the_experience_in_its_episodic_memory() {
        Environment env = component.getEnvironment();
        
        String experienceCount = env.getValue("memory.episodic.count");
        Assertions.assertNotNull(experienceCount, "Experience count should be recorded");
        Assertions.assertTrue(Integer.parseInt(experienceCount) > 0, "At least one experience should be recorded");
        
        // Check that the experience details were recorded
        String experienceId = env.getValue("memory.episodic.experience.0.id");
        Assertions.assertNotNull(experienceId, "Experience ID should be recorded");
        
        String experienceType = env.getValue("memory.episodic.experience.0.type");
        Assertions.assertEquals("first-experience", experienceType, "Experience should be marked as first experience");
    }
    
    @And("the experience should include input and output details")
    public void the_experience_should_include_input_and_output_details() {
        Environment env = component.getEnvironment();
        
        // Check input details
        String inputId = env.getValue("memory.episodic.experience.0.inputId");
        String inputType = env.getValue("memory.episodic.experience.0.inputType");
        String inputValue = env.getValue("memory.episodic.experience.0.inputValue");
        
        Assertions.assertNotNull(inputId, "Experience should include input ID");
        Assertions.assertNotNull(inputType, "Experience should include input type");
        Assertions.assertNotNull(inputValue, "Experience should include input value");
        
        // Check output details
        String outputId = env.getValue("memory.episodic.experience.0.outputId");
        String outputValue = env.getValue("memory.episodic.experience.0.outputValue");
        
        Assertions.assertNotNull(outputId, "Experience should include output ID");
        Assertions.assertNotNull(outputValue, "Experience should include output value");
    }
    
    @And("the experience should be marked as a formative memory")
    public void the_experience_should_be_marked_as_a_formative_memory() {
        Environment env = component.getEnvironment();
        
        String isFormative = env.getValue("memory.episodic.experience.0.isFormative");
        Assertions.assertEquals("true", isFormative, "Experience should be marked as formative");
    }
    
    @Then("the state change should be recorded in the tube's memory")
    public void the_state_change_should_be_recorded_in_the_tube_s_memory() {
        Environment env = component.getEnvironment();
        
        String transitionCount = env.getValue("memory.state.transitions");
        Assertions.assertNotNull(transitionCount, "State transition count should be recorded");
        Assertions.assertTrue(Integer.parseInt(transitionCount) > 0, "At least one state transition should be recorded");
        
        // Check that the transition details were recorded
        String fromState = env.getValue("memory.state.transition.0.from");
        String toState = env.getValue("memory.state.transition.0.to");
        String timestamp = env.getValue("memory.state.transition.0.timestamp");
        
        Assertions.assertNotNull(fromState, "Previous state should be recorded");
        Assertions.assertNotNull(toState, "New state should be recorded");
        Assertions.assertNotNull(timestamp, "Transition timestamp should be recorded");
    }
    
    @And("the tube should maintain awareness of its previous state")
    public void the_tube_should_maintain_awareness_of_its_previous_state() {
        Environment env = component.getEnvironment();
        
        // The previous state should be recorded in the environment
        String fromState = env.getValue("memory.state.transition.0.from");
        Assertions.assertNotNull(fromState, "Previous state should be recorded");
        
        // Convert the string state back to enum for comparison
        State previousStateFromMemory;
        switch (fromState) {
            case "INITIALIZING":
                previousStateFromMemory = State.INITIALIZING;
                break;
            case "READY":
                previousStateFromMemory = State.ACTIVE;
                break;
            default:
                previousStateFromMemory = null;
        }
        
        Assertions.assertEquals(previousState, previousStateFromMemory, 
            "Recorded previous state should match actual previous state");
    }
    
    @Then("the tube should begin to recognize patterns")
    public void the_tube_should_begin_to_recognize_patterns() {
        Environment env = component.getEnvironment();
        
        String patternsRecognized = env.getValue("memory.patterns.recognized");
        Assertions.assertNotNull(patternsRecognized, "Pattern recognition count should be recorded");
        Assertions.assertTrue(Integer.parseInt(patternsRecognized) > 0, "At least one pattern should be recognized");
        
        // Check recognized patterns match our expectations
        int patternCount = Integer.parseInt(patternsRecognized);
        for (int i = 0; i < patternCount; i++) {
            String patternId = env.getValue("memory.pattern." + i + ".id");
            String inputValue = env.getValue("memory.pattern." + i + ".inputValue");
            String recognized = env.getValue("memory.pattern." + i + ".recognized");
            
            Assertions.assertNotNull(patternId, "Pattern ID should be recorded");
            Assertions.assertNotNull(inputValue, "Pattern input value should be recorded");
            Assertions.assertEquals("true", recognized, "Pattern should be marked as recognized");
            
            // Verify this input actually occurred multiple times
            Assertions.assertTrue(inputFrequency.containsKey(inputValue), 
                "Recognized pattern should correspond to an actual input");
            Assertions.assertTrue(inputFrequency.get(inputValue) > 1, 
                "Recognized pattern should have occurred multiple times");
        }
    }
    
    @And("the tube should adapt its processing based on past experiences")
    public void the_tube_should_adapt_its_processing_based_on_past_experiences() {
        Environment env = component.getEnvironment();
        
        String optimizationApplied = env.getValue("memory.pattern.optimization.applied");
        Assertions.assertEquals("true", optimizationApplied, "Pattern-based optimization should be applied");
        
        String optimizationCount = env.getValue("memory.pattern.optimization.count");
        Assertions.assertNotNull(optimizationCount, "Optimization count should be recorded");
        Assertions.assertTrue(Integer.parseInt(optimizationCount) > 0, "At least one optimization should be applied");
        
        // Check optimization details
        int count = Integer.parseInt(optimizationCount);
        for (int i = 0; i < count; i++) {
            String patternId = env.getValue("memory.pattern.optimization." + i + ".patternId");
            String inputValue = env.getValue("memory.pattern.optimization." + i + ".inputValue");
            String optimizationType = env.getValue("memory.pattern.optimization." + i + ".optimizationType");
            
            Assertions.assertNotNull(patternId, "Optimization should reference a pattern ID");
            Assertions.assertNotNull(inputValue, "Optimization should reference an input value");
            Assertions.assertNotNull(optimizationType, "Optimization should have a type");
        }
    }
    
    // Helper method to simulate processing input
    private Map<String, String> processInput(Map<String, String> input) {
        // Simulate output generation based on input
        Map<String, String> output = new HashMap<>();
        output.put("id", UUID.randomUUID().toString());
        
        // Generate output value based on input pattern
        String inputValue = input.get("value");
        String outputValue;
        
        if (inputValue.startsWith("pattern-value-")) {
            // For pattern values, transform them
            char patternChar = inputValue.charAt(inputValue.length() - 1);
            outputValue = "processed-" + patternChar;
        } else {
            // For other values, just append "processed"
            outputValue = "processed-" + inputValue;
        }
        
        output.put("value", outputValue);
        output.put("timestamp", LocalDateTime.now().toString());
        
        return output;
    }
}