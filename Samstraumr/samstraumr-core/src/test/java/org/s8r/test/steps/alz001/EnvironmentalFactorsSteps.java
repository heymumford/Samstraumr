/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.steps.alz001;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions for environmental factor test scenarios.
 * 
 * <p>Implements steps for creating, configuring, and testing environmental factor
 * components and their impact on disease progression.
 */
public class EnvironmentalFactorsSteps extends ALZ001BaseSteps {

    /**
     * Sets up a scientific analysis environment for environmental modeling.
     */
    @Given("a scientific analysis environment for environmental modeling")
    public void scientificAnalysisEnvironmentForEnvironmentalModeling() {
        // Create a mock or real scientific environment for testing
        ScientificAnalysisEnvironment environment = new ScientificAnalysisEnvironment();
        environment.enableCapability("environmental_factor_modeling");
        environment.enableCapability("oxidative_stress_simulation");
        environment.enableCapability("inflammatory_response_modeling");
        
        // Store in context
        storeInContext("environment", environment);
    }
    
    /**
     * Sets up a baseline disease progression model.
     */
    @Given("a baseline disease progression model")
    public void baselineDiseaseProgressionModel() {
        // Create a baseline disease model
        DiseaseProgressionModel model = new DiseaseProgressionModel();
        model.initialize();
        
        // Store in context
        storeInContext("baselineModel", model);
    }
    
    /**
     * Creates an environmental factors composite.
     */
    @When("I create an environmental factors composite")
    public void createEnvironmentalFactorsComposite() {
        // Create the composite
        EnvironmentalFactorsComposite composite = new EnvironmentalFactorsComposite();
        composite.initialize();
        
        // Store in test context
        storeInContext("environmentalComposite", composite);
    }
    
    /**
     * Verifies the composite has environmental modeling capabilities.
     */
    @Then("the composite should have environmental modeling capabilities")
    public void compositeShouldHaveEnvironmentalModelingCapabilities() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        assertTrue(composite.hasCapability("environmental_modeling"), 
                "Composite should have environmental modeling capability");
        assertTrue(composite.hasCapability("factor_interaction"), 
                "Composite should have factor interaction capability");
    }
    
    /**
     * Sets up an initialized environmental factors composite.
     */
    @Given("an initialized environmental factors composite")
    public void initializedEnvironmentalFactorsComposite() {
        createEnvironmentalFactorsComposite();
    }
    
    /**
     * Configures oxidative stress parameters.
     */
    @When("I configure the following oxidative stress parameters:")
    public void configureOxidativeStressParameters(DataTable dataTable) {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Configure parameters
        Map<String, Object> parameters = new HashMap<>();
        for (Map<String, String> row : rows) {
            String parameter = row.get("parameter");
            String valueStr = row.get("value");
            
            // Convert value to appropriate type
            Object value;
            if (valueStr.contains(".")) {
                value = Double.parseDouble(valueStr);
            } else {
                value = Integer.parseInt(valueStr);
            }
            
            parameters.put(parameter, value);
        }
        
        // Apply configuration
        composite.configureOxidativeStress(parameters);
        
        // Store parameters
        storeInContext("oxidativeStressParams", parameters);
    }
    
    /**
     * Verifies the component creates an oxidative stress model.
     */
    @Then("the component should create an oxidative stress model")
    public void componentShouldCreateOxidativeStressModel() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        
        assertTrue(composite.hasModel("oxidative_stress"), 
                "Composite should have created an oxidative stress model");
        
        OxidativeStressModel model = composite.getOxidativeStressModel();
        assertNotNull(model, "Oxidative stress model should not be null");
        assertTrue(model.isInitialized(), "Model should be initialized");
    }
    
    /**
     * Verifies the model includes baseline and fluctuation patterns.
     */
    @Then("the model should include baseline and fluctuation patterns")
    public void modelShouldIncludeBaselineAndFluctuationPatterns() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        OxidativeStressModel model = composite.getOxidativeStressModel();
        
        assertNotNull(model.getBaselineLevel(), "Model should include baseline level");
        assertNotNull(model.getDailyFluctuation(), "Model should include daily fluctuation");
        
        Map<String, Object> params = getFromContext("oxidativeStressParams");
        
        assertEquals(params.get("baseline_level"), model.getBaselineLevel(), 
                "Baseline level should match configured value");
        assertEquals(params.get("daily_fluctuation"), model.getDailyFluctuation(), 
                "Daily fluctuation should match configured value");
    }
    
    /**
     * Sets up an environmental factors composite with oxidative stress configured.
     */
    @Given("an environmental factors composite with oxidative stress configured")
    public void environmentalFactorsCompositeWithOxidativeStressConfigured() {
        initializedEnvironmentalFactorsComposite();
        
        // Configure oxidative stress with default parameters
        List<Map<String, String>> rows = List.of(
                Map.of("parameter", "baseline_level", "value", "1.0"),
                Map.of("parameter", "daily_fluctuation", "value", "0.2"),
                Map.of("parameter", "stress_response_threshold", "value", "2.5"),
                Map.of("parameter", "chronic_threshold", "value", "3.0"),
                Map.of("parameter", "acute_duration_days", "value", "5")
        );
        
        DataTable dataTable = DataTable.create(rows);
        configureOxidativeStressParameters(dataTable);
    }
    
    /**
     * Configures inflammatory response with triggers.
     */
    @When("I configure inflammatory response with the following triggers:")
    public void configureInflammatoryResponseWithTriggers(DataTable dataTable) {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Configure triggers
        List<InflammationTrigger> triggers = new ArrayList<>();
        for (Map<String, String> row : rows) {
            String name = row.get("trigger");
            double threshold = Double.parseDouble(row.get("threshold"));
            int delayHours = Integer.parseInt(row.get("delay_hours"));
            int durationDays = Integer.parseInt(row.get("duration_days"));
            double intensity = Double.parseDouble(row.get("intensity"));
            
            triggers.add(new InflammationTrigger(name, threshold, delayHours, durationDays, intensity));
        }
        
        // Apply configuration
        composite.configureInflammatoryResponse(triggers);
        
        // Store triggers
        storeInContext("inflammationTriggers", triggers);
    }
    
    /**
     * Verifies each trigger is properly modeled.
     */
    @Then("each trigger should be properly modeled")
    public void eachTriggerShouldBeProperlyModeled() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        List<InflammationTrigger> expectedTriggers = getFromContext("inflammationTriggers");
        
        InflammatoryResponseModel model = composite.getInflammatoryResponseModel();
        assertNotNull(model, "Inflammatory response model should not be null");
        
        List<InflammationTrigger> actualTriggers = model.getTriggers();
        assertEquals(expectedTriggers.size(), actualTriggers.size(), 
                "Number of triggers should match configured count");
        
        // Verify first trigger
        InflammationTrigger expectedFirst = expectedTriggers.get(0);
        InflammationTrigger actualFirst = actualTriggers.get(0);
        
        assertEquals(expectedFirst.getName(), actualFirst.getName(), 
                "Trigger name should match");
        assertEquals(expectedFirst.getThreshold(), actualFirst.getThreshold(), 0.001, 
                "Trigger threshold should match");
    }
    
    /**
     * Verifies trigger thresholds determine activation.
     */
    @Then("trigger thresholds should determine activation")
    public void triggerThresholdsShouldDetermineActivation() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        InflammatoryResponseModel model = composite.getInflammatoryResponseModel();
        
        // Test below threshold
        InflammationTrigger firstTrigger = model.getTriggers().get(0);
        double belowThreshold = firstTrigger.getThreshold() - 0.1;
        
        assertFalse(model.isTriggerActivated(firstTrigger.getName(), belowThreshold), 
                "Trigger should not activate below threshold");
        
        // Test above threshold
        double aboveThreshold = firstTrigger.getThreshold() + 0.1;
        assertTrue(model.isTriggerActivated(firstTrigger.getName(), aboveThreshold), 
                "Trigger should activate above threshold");
    }
    
    /**
     * Verifies delayed responses are temporally accurate.
     */
    @Then("delayed responses should be temporally accurate")
    public void delayedResponsesShouldBeTemporallyAccurate() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        InflammatoryResponseModel model = composite.getInflammatoryResponseModel();
        
        InflammationTrigger firstTrigger = model.getTriggers().get(0);
        
        // Check before delay period
        int beforeDelay = firstTrigger.getDelayHours() - 1;
        assertEquals(0.0, model.getResponseIntensity(firstTrigger.getName(), beforeDelay), 0.001, 
                "Response intensity should be zero before delay period");
        
        // Check after delay period
        int afterDelay = firstTrigger.getDelayHours() + 1;
        assertTrue(model.getResponseIntensity(firstTrigger.getName(), afterDelay) > 0.0, 
                "Response intensity should be positive after delay period");
    }
    
    /**
     * Verifies inflammatory intensity scales with trigger strength.
     */
    @Then("inflammatory intensity should scale with trigger strength")
    public void inflammatoryIntensityShouldScaleWithTriggerStrength() {
        EnvironmentalFactorsComposite composite = getFromContext("environmentalComposite");
        InflammatoryResponseModel model = composite.getInflammatoryResponseModel();
        
        InflammationTrigger trigger = model.getTriggers().get(0);
        int timePoint = trigger.getDelayHours() + 24; // 24 hours after delay
        
        // Compare intensities with different trigger strengths
        double baseStrength = trigger.getThreshold() + 0.1;
        double higherStrength = trigger.getThreshold() + 0.3;
        
        double baseIntensity = model.getResponseIntensity(trigger.getName(), timePoint, baseStrength);
        double higherIntensity = model.getResponseIntensity(trigger.getName(), timePoint, higherStrength);
        
        assertTrue(higherIntensity > baseIntensity, 
                "Higher trigger strength should produce higher response intensity");
    }
    
    /**
     * Mock class representing an environmental factors composite.
     */
    public static class EnvironmentalFactorsComposite {
        private boolean initialized = false;
        private String state = "CREATED";
        private final Map<String, String> capabilities = new HashMap<>();
        private OxidativeStressModel oxidativeStressModel;
        private InflammatoryResponseModel inflammatoryResponseModel;
        private final Map<String, Object> models = new HashMap<>();
        
        public void initialize() {
            this.initialized = true;
            this.state = "READY";
            
            // Set default capabilities
            capabilities.put("environmental_modeling", "true");
            capabilities.put("factor_interaction", "true");
            capabilities.put("time_series_analysis", "true");
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public boolean hasCapability(String capability) {
            return capabilities.containsKey(capability) && 
                   "true".equals(capabilities.get(capability));
        }
        
        public String getState() {
            return state;
        }
        
        public void configureOxidativeStress(Map<String, Object> parameters) {
            oxidativeStressModel = new OxidativeStressModel(parameters);
            models.put("oxidative_stress", oxidativeStressModel);
        }
        
        public boolean hasModel(String modelType) {
            return models.containsKey(modelType);
        }
        
        public OxidativeStressModel getOxidativeStressModel() {
            return oxidativeStressModel;
        }
        
        public void configureInflammatoryResponse(List<InflammationTrigger> triggers) {
            inflammatoryResponseModel = new InflammatoryResponseModel(triggers);
            models.put("inflammatory_response", inflammatoryResponseModel);
        }
        
        public InflammatoryResponseModel getInflammatoryResponseModel() {
            return inflammatoryResponseModel;
        }
    }
    
    /**
     * Mock class representing a disease progression model.
     */
    public static class DiseaseProgressionModel {
        private boolean initialized = false;
        
        public void initialize() {
            this.initialized = true;
        }
        
        public boolean isInitialized() {
            return initialized;
        }
    }
    
    /**
     * Mock class representing an oxidative stress model.
     */
    public static class OxidativeStressModel {
        private final Map<String, Object> parameters;
        private boolean initialized = true;
        
        public OxidativeStressModel(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public Double getBaselineLevel() {
            return (Double) parameters.get("baseline_level");
        }
        
        public Double getDailyFluctuation() {
            return (Double) parameters.get("daily_fluctuation");
        }
        
        public Double getStressResponseThreshold() {
            return (Double) parameters.get("stress_response_threshold");
        }
    }
    
    /**
     * Mock class representing an inflammatory response model.
     */
    public static class InflammatoryResponseModel {
        private final List<InflammationTrigger> triggers;
        
        public InflammatoryResponseModel(List<InflammationTrigger> triggers) {
            this.triggers = triggers;
        }
        
        public List<InflammationTrigger> getTriggers() {
            return triggers;
        }
        
        public boolean isTriggerActivated(String triggerName, double strength) {
            for (InflammationTrigger trigger : triggers) {
                if (trigger.getName().equals(triggerName)) {
                    return strength >= trigger.getThreshold();
                }
            }
            return false;
        }
        
        public double getResponseIntensity(String triggerName, int timeHours) {
            return getResponseIntensity(triggerName, timeHours, 
                    getTriggerByName(triggerName).getThreshold() + 0.1);
        }
        
        public double getResponseIntensity(String triggerName, int timeHours, double strength) {
            InflammationTrigger trigger = getTriggerByName(triggerName);
            
            if (trigger == null || !isTriggerActivated(triggerName, strength)) {
                return 0.0;
            }
            
            if (timeHours < trigger.getDelayHours()) {
                return 0.0;
            }
            
            double maxIntensity = trigger.getIntensity() * 
                    (strength - trigger.getThreshold()) / trigger.getThreshold();
            
            int activationHours = timeHours - trigger.getDelayHours();
            int maxDurationHours = trigger.getDurationDays() * 24;
            
            if (activationHours > maxDurationHours) {
                return 0.0;
            }
            
            // Simple bell curve for intensity
            return maxIntensity * Math.sin(Math.PI * activationHours / maxDurationHours);
        }
        
        private InflammationTrigger getTriggerByName(String name) {
            for (InflammationTrigger trigger : triggers) {
                if (trigger.getName().equals(name)) {
                    return trigger;
                }
            }
            return null;
        }
    }
    
    /**
     * Class representing an inflammation trigger.
     */
    public static class InflammationTrigger {
        private final String name;
        private final double threshold;
        private final int delayHours;
        private final int durationDays;
        private final double intensity;
        
        public InflammationTrigger(String name, double threshold, int delayHours, 
                                  int durationDays, double intensity) {
            this.name = name;
            this.threshold = threshold;
            this.delayHours = delayHours;
            this.durationDays = durationDays;
            this.intensity = intensity;
        }
        
        public String getName() {
            return name;
        }
        
        public double getThreshold() {
            return threshold;
        }
        
        public int getDelayHours() {
            return delayHours;
        }
        
        public int getDurationDays() {
            return durationDays;
        }
        
        public double getIntensity() {
            return intensity;
        }
    }
}