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
 * Step definitions for predictive modeling test scenarios.
 * 
 * <p>Implements steps for creating, training, and evaluating predictive
 * models for disease progression.
 */
public class PredictiveModelingSteps extends ALZ001BaseSteps {

    /**
     * Sets up a scientific analysis environment for predictive modeling.
     */
    @Given("a scientific analysis environment for predictive modeling")
    public void scientificAnalysisEnvironmentForPredictiveModeling() {
        // Create a mock or real scientific environment for testing
        ScientificAnalysisEnvironment environment = new ScientificAnalysisEnvironment();
        environment.enableCapability("predictive_modeling");
        environment.enableCapability("machine_learning");
        environment.enableCapability("feature_engineering");
        
        // Store in context
        storeInContext("environment", environment);
    }
    
    /**
     * Sets up training data from multiple patient cohorts.
     */
    @Given("training data from multiple patient cohorts")
    public void trainingDataFromMultiplePatientCohorts() {
        // Create training dataset
        PatientCohortDataset dataset = new PatientCohortDataset();
        dataset.loadSampleCohorts(3);  // 3 cohorts
        
        // Store in context
        storeInContext("trainingData", dataset);
    }
    
    /**
     * Creates a predictive modeling machine.
     */
    @When("I create a predictive modeling machine")
    public void createPredictiveModelingMachine() {
        // Create the machine
        PredictiveModelingMachine machine = new PredictiveModelingMachine();
        machine.initialize();
        
        // Store in test context
        storeInContext("predictiveMachine", machine);
    }
    
    /**
     * Verifies the machine contains specified components.
     */
    @Then("the machine should contain the following components:")
    public void machineShouldContainComponents(DataTable dataTable) {
        PredictiveModelingMachine machine = getFromContext("predictiveMachine");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Verify each component exists
        for (Map<String, String> row : rows) {
            String componentType = row.get("component_type");
            assertTrue(machine.hasComponent(componentType),
                    "Machine should contain component: " + componentType);
        }
    }
    
    /**
     * Sets up a configured predictive modeling machine with training data.
     */
    @Given("a configured predictive modeling machine with training data")
    public void configuredPredictiveModelingMachineWithTrainingData() {
        // Create and initialize machine
        createPredictiveModelingMachine();
        
        // Get machine and training data
        PredictiveModelingMachine machine = getFromContext("predictiveMachine");
        
        // Create and load training data if not already in context
        if (!testContext.containsKey("trainingData")) {
            PatientCohortDataset dataset = new PatientCohortDataset();
            dataset.loadSampleCohorts(3);  // 3 cohorts
            storeInContext("trainingData", dataset);
        }
        
        PatientCohortDataset trainingData = getFromContext("trainingData");
        
        // Configure machine
        Map<String, Object> config = new HashMap<>();
        config.put("default_algorithm", "ensemble");
        config.put("cross_validation_folds", 5);
        config.put("feature_selection_method", "recursive");
        config.put("missing_data_strategy", "imputation");
        
        machine.configure(config);
        machine.loadData(trainingData);
        
        // Mark as configured
        storeInContext("machineConfigured", true);
    }
    
    /**
     * Trains a short-term model with specified parameters.
     */
    @When("I train a short-term model with the following parameters:")
    public void trainShortTermModelWithParameters(DataTable dataTable) {
        PredictiveModelingMachine machine = getFromContext("predictiveMachine");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Configure parameters
        Map<String, Object> parameters = new HashMap<>();
        for (Map<String, String> row : rows) {
            String parameter = row.get("parameter");
            String valueStr = row.get("value");
            
            // Convert value to appropriate type
            Object value;
            if (valueStr.matches("\\d+\\.\\d+")) {
                value = Double.parseDouble(valueStr);
            } else if (valueStr.matches("\\d+")) {
                value = Integer.parseInt(valueStr);
            } else {
                value = valueStr;
            }
            
            parameters.put(parameter, value);
        }
        
        // Train the model
        machine.trainModel(parameters);
        
        // Store model parameters
        storeInContext("modelParameters", parameters);
        storeInContext("modelTrained", true);
    }
    
    /**
     * Verifies the model completes training successfully.
     */
    @Then("the model should complete training successfully")
    public void modelShouldCompleteTrainingSuccessfully() {
        PredictiveModelingMachine machine = getFromContext("predictiveMachine");
        
        assertTrue(machine.isModelTrained(), "Model should be trained");
        assertNotNull(machine.getTrainedModel(), "Trained model should not be null");
        assertEquals("TRAINED", machine.getModelState(), "Model state should be TRAINED");
    }
    
    /**
     * Verifies validation metrics meet minimum thresholds.
     */
    @Then("validation metrics should include:")
    public void validationMetricsShouldInclude(DataTable dataTable) {
        PredictiveModelingMachine machine = getFromContext("predictiveMachine");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Get model performance metrics
        Map<String, Double> metrics = machine.getPerformanceMetrics();
        
        // Verify each metric meets the threshold
        for (Map<String, String> row : rows) {
            String metric = row.get("metric");
            double threshold = Double.parseDouble(row.get("minimum_threshold"));
            
            assertTrue(metrics.containsKey(metric), 
                    "Performance metrics should include " + metric);
            assertTrue(metrics.get(metric) >= threshold, 
                    "Metric " + metric + " should meet minimum threshold");
        }
    }
    
    /**
     * Verifies the model identifies key predictive factors.
     */
    @Then("the model should identify key predictive factors")
    public void modelShouldIdentifyKeyPredictiveFactors() {
        PredictiveModelingMachine machine = getFromContext("predictiveMachine");
        
        List<FeatureImportance> features = machine.getFeatureImportance();
        
        assertNotNull(features, "Feature importance should not be null");
        assertFalse(features.isEmpty(), "Feature importance list should not be empty");
        assertTrue(features.size() >= 3, "Should identify at least 3 important features");
        
        // Verify features are ordered by importance
        double previousImportance = Double.MAX_VALUE;
        for (FeatureImportance feature : features) {
            assertTrue(feature.getImportance() <= previousImportance, 
                    "Features should be ordered by decreasing importance");
            previousImportance = feature.getImportance();
        }
    }
    
    /**
     * Sets up a trained predictive modeling machine.
     */
    @Given("a trained predictive modeling machine")
    public void trainedPredictiveModelingMachine() {
        configuredPredictiveModelingMachineWithTrainingData();
        
        // Train with default parameters if not already trained
        if (!Boolean.TRUE.equals(getFromContext("modelTrained"))) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("prediction_horizon_days", 90);
            parameters.put("training_window_days", 365);
            parameters.put("validation_split", 0.2);
            parameters.put("feature_set", "comprehensive");
            parameters.put("algorithm", "ensemble");
            
            PredictiveModelingMachine machine = getFromContext("predictiveMachine");
            machine.trainModel(parameters);
            
            storeInContext("modelParameters", parameters);
            storeInContext("modelTrained", true);
        }
    }
    
    /**
     * Mock class representing a patient cohort dataset.
     */
    public static class PatientCohortDataset {
        private final List<PatientCohort> cohorts = new ArrayList<>();
        
        public void loadSampleCohorts(int count) {
            for (int i = 0; i < count; i++) {
                cohorts.add(new PatientCohort("cohort_" + (i+1), 100));
            }
        }
        
        public List<PatientCohort> getCohorts() {
            return cohorts;
        }
        
        public int getTotalPatients() {
            return cohorts.stream().mapToInt(PatientCohort::getPatientCount).sum();
        }
    }
    
    /**
     * Mock class representing a patient cohort.
     */
    public static class PatientCohort {
        private final String name;
        private final int patientCount;
        
        public PatientCohort(String name, int patientCount) {
            this.name = name;
            this.patientCount = patientCount;
        }
        
        public String getName() {
            return name;
        }
        
        public int getPatientCount() {
            return patientCount;
        }
    }
    
    /**
     * Mock class representing a predictive modeling machine.
     */
    public static class PredictiveModelingMachine {
        private boolean initialized = false;
        private String state = "CREATED";
        private final Map<String, Object> components = new HashMap<>();
        private final Map<String, Object> configuration = new HashMap<>();
        private PatientCohortDataset data;
        private boolean modelTrained = false;
        private PredictiveModel trainedModel;
        private String modelState = "UNTRAINED";
        
        public void initialize() {
            this.initialized = true;
            this.state = "READY";
            
            // Create components
            components.put("data_preprocessing", new Object());
            components.put("feature_extraction", new Object());
            components.put("model_training", new Object());
            components.put("model_evaluation", new Object());
            components.put("prediction_generation", new Object());
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public boolean hasComponent(String componentType) {
            return components.containsKey(componentType);
        }
        
        public String getState() {
            return state;
        }
        
        public void configure(Map<String, Object> config) {
            configuration.putAll(config);
            state = "CONFIGURED";
        }
        
        public void loadData(PatientCohortDataset data) {
            this.data = data;
            state = "DATA_LOADED";
        }
        
        public void trainModel(Map<String, Object> parameters) {
            if (data == null) {
                throw new IllegalStateException("Data must be loaded before training");
            }
            
            // Create trained model
            trainedModel = new PredictiveModel(parameters);
            modelTrained = true;
            modelState = "TRAINED";
            
            // Generate performance metrics
            Map<String, Double> metrics = new HashMap<>();
            metrics.put("accuracy", 0.82);
            metrics.put("precision", 0.78);
            metrics.put("recall", 0.76);
            metrics.put("f1_score", 0.77);
            metrics.put("area_under_curve", 0.85);
            
            trainedModel.setPerformanceMetrics(metrics);
            
            // Generate feature importance
            List<FeatureImportance> features = new ArrayList<>();
            features.add(new FeatureImportance("tau_protein_level", 0.85));
            features.add(new FeatureImportance("hippocampal_volume", 0.72));
            features.add(new FeatureImportance("amyloid_beta_ratio", 0.68));
            features.add(new FeatureImportance("cognitive_test_score", 0.55));
            features.add(new FeatureImportance("age", 0.48));
            
            trainedModel.setFeatureImportance(features);
        }
        
        public boolean isModelTrained() {
            return modelTrained;
        }
        
        public PredictiveModel getTrainedModel() {
            return trainedModel;
        }
        
        public String getModelState() {
            return modelState;
        }
        
        public Map<String, Double> getPerformanceMetrics() {
            if (trainedModel == null) {
                return new HashMap<>();
            }
            return trainedModel.getPerformanceMetrics();
        }
        
        public List<FeatureImportance> getFeatureImportance() {
            if (trainedModel == null) {
                return new ArrayList<>();
            }
            return trainedModel.getFeatureImportance();
        }
    }
    
    /**
     * Mock class representing a predictive model.
     */
    public static class PredictiveModel {
        private final Map<String, Object> parameters;
        private Map<String, Double> performanceMetrics = new HashMap<>();
        private List<FeatureImportance> featureImportance = new ArrayList<>();
        
        public PredictiveModel(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setPerformanceMetrics(Map<String, Double> metrics) {
            this.performanceMetrics = metrics;
        }
        
        public Map<String, Double> getPerformanceMetrics() {
            return performanceMetrics;
        }
        
        public void setFeatureImportance(List<FeatureImportance> features) {
            this.featureImportance = features;
        }
        
        public List<FeatureImportance> getFeatureImportance() {
            return featureImportance;
        }
    }
    
    /**
     * Class representing feature importance in a predictive model.
     */
    public static class FeatureImportance {
        private final String featureName;
        private final double importance;
        
        public FeatureImportance(String featureName, double importance) {
            this.featureName = featureName;
            this.importance = importance;
        }
        
        public String getFeatureName() {
            return featureName;
        }
        
        public double getImportance() {
            return importance;
        }
    }
}